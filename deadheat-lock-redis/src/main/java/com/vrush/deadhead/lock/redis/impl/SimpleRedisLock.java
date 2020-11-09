package com.vrush.deadhead.lock.redis.impl;

import com.vrush.deadheat.lock.AbstractSimpleLock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * works with single key
 */
@Slf4j
public class SimpleRedisLock extends AbstractSimpleLock {

  private static String LOCK_SCRIPT="";
  private static String LOCK_RELEASE_SCRIPT="";
  private static String LOCK_REFRESH_SCRIPT="";

  static {
    try {
      LOCK_SCRIPT = new BufferedReader(new InputStreamReader(new ClassPathResource("lockscript/singlelock/LOCK.lua").getInputStream()))
        .lines().collect(Collectors.joining("\n"));
      LOCK_RELEASE_SCRIPT = new BufferedReader(new InputStreamReader(new ClassPathResource("lockscript/singlelock/RELEASE.lua").getInputStream()))
        .lines().collect(Collectors.joining("\n"));
      LOCK_REFRESH_SCRIPT = new BufferedReader(new InputStreamReader(new ClassPathResource("lockscript/singlelock/REFRESH.lua").getInputStream()))
        .lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new IllegalStateException("Cannot lock as redis script not readable");
    }
  }

  private final RedisScript<Boolean> lockScript = new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class);
  private final RedisScript<Boolean> lockReleaseScript = new DefaultRedisScript<>(LOCK_RELEASE_SCRIPT, Boolean.class);
  private final RedisScript<Boolean> lockRefreshScript = new DefaultRedisScript<>(LOCK_REFRESH_SCRIPT, Boolean.class);

  private final StringRedisTemplate stringRedisTemplate;

  public SimpleRedisLock(final Supplier<String> tokenSupplier, final StringRedisTemplate stringRedisTemplate) {
    super(tokenSupplier);
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected String acquire(final String key, final String storeId, final String token, final long expiration) {
    final List<String> singletonKeyList = Collections.singletonList(storeId + ":" + key);
    final boolean locked = stringRedisTemplate.execute(lockScript, singletonKeyList, token, String.valueOf(expiration));
    log.debug("Tried to acquire lock for key {} with token {} in store {}. Locked: {}", key, token, storeId, locked);
    return locked ? token : null;
  }

  @Override
  protected boolean release(final String key, final String storeId, final String token) {
    final List<String> singletonKeyList = Collections.singletonList(storeId + ":" + key);

    final boolean released = stringRedisTemplate.execute(lockReleaseScript, singletonKeyList, token);
    if (released) {
      log.debug("Release script deleted the record for key {} with token {} in store {}", key, token, storeId);
    } else {
      log.error("Release script failed for key {} with token {} in store {}", key, token, storeId);
    }
    return released;
  }

  @Override
  protected boolean refresh(final String key, final String storeId, final String token, final long expiration) {
    final List<String> singletonKeyList = Collections.singletonList(storeId + ":" + key);

    final boolean refreshed = stringRedisTemplate.execute(lockRefreshScript, singletonKeyList, token, String.valueOf(expiration));
    if (refreshed) {
      log.debug("Refresh script updated the expiration for key {} with token {} in store {} to {}", key, token, storeId, expiration);
    } else {
      log.debug("Refresh script failed to update expiration for key {} with token {} in store {} with expiration: {}", key, token, storeId, expiration);
    }
    return refreshed;
  }
}