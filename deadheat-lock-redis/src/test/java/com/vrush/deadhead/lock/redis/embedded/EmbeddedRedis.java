/**
 * modified by @author Vrushabh Joshi
 * last on 15/11/2020
 */
package com.vrush.deadhead.lock.redis.embedded;

import java.io.IOException;
import java.net.ServerSocket;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

@Component
public class EmbeddedRedis {

  private RedisServer server;

  @PostConstruct
  public void start() throws IOException {
    // get a free port
    final ServerSocket serverSocket = new ServerSocket(0);
    final int port = serverSocket.getLocalPort();
    serverSocket.close();

    server = RedisServer.builder().setting("bind 127.0.0.1").port(port).build();
    server.start();

    final LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", port));
    connectionFactory.setDatabase(0);
    connectionFactory.afterPropertiesSet();

    System.setProperty("spring.redis.port", String.valueOf(port));
  }

  @PreDestroy
  public void stop() {
    server.stop();
    System.clearProperty("spring.redis.port");
  }
}