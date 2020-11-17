/**
 * modified by @author Vrushabh Joshi
 * last on 15/11/2020
 */
package com.vrush.deadhead.lock.redis.impl;

import com.vrush.deadhead.lock.redis.embedded.EmbeddedRedis;
import com.vrush.deadheat.lock.Lock;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class MultiRedisLockTest implements InitializingBean {

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
  public void shouldLockSingleKey() {
    final String token = lock.acquire(Collections.singletonList("1"), "locks", 1000);
    assertThat(token).isEqualTo("vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("vrush");
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
  }

  @Test
  public void shouldLockMultipleKeys() {
    final String token = lock.acquire(Arrays.asList("1", "2"), "locks", 1000);
    assertThat(token).isEqualTo("vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("vrush");
    assertThat(redisTemplate.opsForValue().get("locks:2")).isEqualTo("vrush");
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
    assertThat(redisTemplate.getExpire("locks:2", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
  }

  @Test
  public void shouldNotLockWhenWholeLockIsTaken() {
    redisTemplate.opsForValue().set("locks:1", "joshi");
    redisTemplate.opsForValue().set("locks:2", "joshi1");

    final String token = lock.acquire(Arrays.asList("1", "2"), "locks", 1000);
    assertThat(token).isNull();
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("joshi");
    assertThat(redisTemplate.opsForValue().get("locks:2")).isEqualTo("joshi1");
  }

  @Test
  public void shouldNotLockWhenLockIsPartiallyTaken() {
    redisTemplate.opsForValue().set("locks:1", "joshi");

    final String token = lock.acquire(Arrays.asList("1", "2"), "locks", 1000);
    assertThat(token).isNull();
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("joshi");
    assertThat(redisTemplate.opsForValue().get("locks:2")).isNull();
  }

  @Test
  public void shouldReleaseSingleKey() {
    redisTemplate.opsForValue().set("locks:1", "vrush");
    lock.release(Collections.singletonList("1"), "locks", "vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isNull();
  }

  @Test
  public void shouldReleaseMultipleKeys() {
    redisTemplate.opsForValue().set("locks:1", "vrush");
    redisTemplate.opsForValue().set("locks:2", "vrush");
    lock.release(Arrays.asList("1", "2"), "locks", "vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isNull();
    assertThat(redisTemplate.opsForValue().get("locks:2")).isNull();
  }

  @Test
  public void shouldNotReleaseWhenTokenDoesNotFullyMatch() {
    redisTemplate.opsForValue().set("locks:1", "joshi");
    redisTemplate.opsForValue().set("locks:2", "joshi1");
    lock.release(Arrays.asList("1", "2"), "locks", "vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("joshi");
    assertThat(redisTemplate.opsForValue().get("locks:2")).isEqualTo("joshi1");
  }

  @Test
  public void shouldNotReleaseWhenTokenDoesNotPartiallyMatch() {
    redisTemplate.opsForValue().set("locks:1", "joshi");
    lock.release(Arrays.asList("1", "2"), "locks", "vrush");
    assertThat(redisTemplate.opsForValue().get("locks:1")).isEqualTo("joshi");
    assertThat(redisTemplate.opsForValue().get("locks:2")).isNull();
  }

  @Test
  public void shouldRefresh() throws InterruptedException {
    final List<String> keys = Arrays.asList("1", "2");

    final String token = lock.acquire(keys, "locks", 1000);
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
    assertThat(redisTemplate.getExpire("locks:2", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
    Thread.sleep(500);
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(500, Offset.offset(100L));
    assertThat(redisTemplate.getExpire("locks:2", TimeUnit.MILLISECONDS)).isCloseTo(500, Offset.offset(100L));
    assertThat(lock.refresh(keys, "locks", token, 1000)).isTrue();
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
    assertThat(redisTemplate.getExpire("locks:2", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
  }

  @Test
  public void shouldNotRefreshBecauseOneKeyExpired() {
    final List<String> keys = Arrays.asList("1", "2");

    final String token = lock.acquire(keys, "locks", 1000);
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
    assertThat(redisTemplate.getExpire("locks:2", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));

    redisTemplate.delete("locks:2");

    assertThat(lock.refresh(keys, "locks", token, 1000)).isFalse();
    assertThat(redisTemplate.getExpire("locks:1", TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(100L));
    assertThat(redisTemplate.opsForValue().get("locks:2")).isNull();
  }

  @Test
  public void shouldExpire() throws InterruptedException {
    lock.acquire(Arrays.asList("1", "2"), "locks", 100);
    Thread.sleep(100);
    assertThat(redisTemplate.opsForValue().get("locks:1")).isNull();
    assertThat(redisTemplate.opsForValue().get("locks:2")).isNull();
  }

  @SpringBootApplication(scanBasePackageClasses = EmbeddedRedis.class)
  static class TestApplication {
  }
}
