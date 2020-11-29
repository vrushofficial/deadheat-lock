package com.vrush.example.deadheatelementaryexample.config;

import com.vrush.deadhead.lock.redis.configuration.RedisDeadHeatLock;
import com.vrush.example.deadheatelementaryexample.service.HelloService;
import com.vrush.example.deadheatelementaryexample.service.LockedHelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * modified by @author Vrushabh Joshi last on 28-11-2020 09:51
 */
@Configuration
@RedisDeadHeatLock
public class ApplicationConfiguration {

    @Bean
    public HelloService helloService() {
        return new LockedHelloService();
    }
}
