/**
 * modified by @author Vrushabh Joshi
 * last on 15/11/2020
 */
package com.vrush.deadhead.lock.redis.impl;

import com.vrush.deadhead.lock.redis.embedded.EmbeddedRedis;
import com.vrush.deadhead.lock.redis.impl.MultiRedisLock;
import com.vrush.deadheat.lock.Lock;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SimpleRedisLockTest implements InitializingBean {

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private StringRedisTemplate redisTemplate;

  private Lock lock;

  @Override
  public void afterPropertiesSet() {
    lock = new MultiRedisLock(redisTemplate, () -> "vrush");
  }

  @Before
  public void cleanRedis() {
    redisTemplate.execute((RedisCallback<?>) connection -> {
      connection.flushDb();
      return null;
    });
  }

  @Test
  public void shouldLock() {
    final String token = lock.acquire(Collections.singletonList("1"), "locks", 1000);
    assertThat(token).isEqualTo("vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("vrush");
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(150L));
  }

  @SpringBootApplication(scanBasePackageClasses = EmbeddedRedis.class)
  static class TestApplication {
  }
}
