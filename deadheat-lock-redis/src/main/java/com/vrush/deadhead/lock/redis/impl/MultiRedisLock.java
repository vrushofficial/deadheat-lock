/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock.redis.impl;

import com.vrush.deadheat.lock.Lock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@AllArgsConstructor
public class MultiRedisLock implements Lock {

  private static String LOCK_SCRIPT="";
  private static String LOCK_RELEASE_SCRIPT="";
  private static String LOCK_REFRESH_SCRIPT="";

  static {
    try {
      LOCK_SCRIPT = new BufferedReader(new InputStreamReader(new ClassPathResource("lockscript/multilock/LOCK.lua").getInputStream()))
        .lines().collect(Collectors.joining("\n"));
      LOCK_RELEASE_SCRIPT = new BufferedReader(new InputStreamReader(new ClassPathResource("lockscript/multilock/RELEASE.lua").getInputStream()))
        .lines().collect(Collectors.joining("\n"));
      LOCK_REFRESH_SCRIPT = new BufferedReader(new InputStreamReader(new ClassPathResource("lockscript/multilock/REFRESH.lua").getInputStream()))
        .lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new IllegalStateException("Cannot lock as redis script not readable");
    }
  }

  private final RedisScript<Boolean> lockScript = new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class);
  private final RedisScript<Boolean> lockReleaseScript = new DefaultRedisScript<>(LOCK_RELEASE_SCRIPT, Boolean.class);
  private final RedisScript<Boolean> lockRefreshScript = new DefaultRedisScript<>(LOCK_REFRESH_SCRIPT, Boolean.class);

  private final StringRedisTemplate stringRedisTemplate;
  private final Supplier<String> tokenSupplier;

  public MultiRedisLock(final StringRedisTemplate stringRedisTemplate) {
    this(stringRedisTemplate, () -> UUID.randomUUID().toString());
  }

  @Override
  public String acquire(final List<String> keys, final String storeId, final long expiration){

    final List<String> keysWithStoreIdPrefix = keys.stream().map(key -> storeId + ":" + key).collect(Collectors.toList());
    final String token = tokenSupplier.get();

    if (StringUtils.isEmpty(token)) {
      throw new IllegalStateException("Cannot lock with empty token");
    }

    final boolean locked = stringRedisTemplate.execute(lockScript, keysWithStoreIdPrefix, token, String.valueOf(expiration));
    log.debug("Tried to acquire lock for keys {} in store {} with token {}. Locked: {}", keys, storeId, token, locked);
    return locked ? token : null;
  }

  @Override
  public boolean release(final List<String> keys, final String storeId, final String token) {
    final List<String> keysWithStoreIdPrefix = keys.stream().map(key -> storeId + ":" + key).collect(Collectors.toList());

    final boolean released = stringRedisTemplate.execute(lockReleaseScript, keysWithStoreIdPrefix, token);
    if (released) {
      log.debug("Release script deleted the record for keys {} with token {} in store {}", keys, token, storeId);
    } else {
      log.error("Release script failed for keys {} with token {} in store {}", keys, token, storeId);
    }
    return released;
  }

  @Override
  public boolean refresh(final List<String> keys, final String storeId, final String token, final long expiration) {
    final List<String> keysWithStoreIdPrefix = keys.stream().map(key -> storeId + ":" + key).collect(Collectors.toList());

    final boolean refreshed = stringRedisTemplate.execute(lockRefreshScript, keysWithStoreIdPrefix, token, String.valueOf(expiration));
    if (refreshed) {
      log.debug("Refresh script refreshed the expiration for keys {} with token {} in store {}", keys, token, storeId);
    } else {
      log.debug("Refresh script failed to update expiration for keys {} with token {} in store {}", keys, token, storeId);
    }
    return refreshed;
  }
}
