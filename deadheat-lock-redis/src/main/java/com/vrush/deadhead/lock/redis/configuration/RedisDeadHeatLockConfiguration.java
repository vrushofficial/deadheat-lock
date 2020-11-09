/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock.redis.configuration;

import com.vrush.deadhead.lock.redis.impl.MultiRedisLock;
import com.vrush.deadhead.lock.redis.impl.SimpleRedisLock;
import com.vrush.deadheat.lock.Lock;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisDeadHeatLockConfiguration {

  @Bean
  public Lock simpleRedisLock(final StringRedisTemplate stringRedisTemplate) {
    return new SimpleRedisLock(() -> UUID.randomUUID().toString(), stringRedisTemplate);
  }

}
