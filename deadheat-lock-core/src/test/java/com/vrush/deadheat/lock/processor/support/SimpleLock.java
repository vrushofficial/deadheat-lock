 /**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.processor.support;
import com.vrush.deadheat.lock.Lock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Data
@Slf4j
public class SimpleLock implements Lock {
  private final Map<String, List<LockedKey>> lockMap = new HashMap<>();

  @Override
  public String acquire(final List<String> keys, final String storeId, final long expiration) {
    log.debug("Acquiring lock for keys {} in store {} with expiration: {}", keys, storeId, expiration);

    final List<LockedKey> lockedKeysWithExpiration = lockMap.get(storeId);
    if (lockedKeysWithExpiration != null) {
      final List<String> locksForStore = lockedKeysWithExpiration.stream()
        .map(LockedKey::getKey)
        .collect(Collectors.toList());

      if (keys.stream().anyMatch(locksForStore::contains)) {
        return null;
      }
    }

    final List<LockedKey> lockedKeys = lockMap.computeIfAbsent(storeId, s -> new ArrayList<>());
    keys.forEach(key -> lockedKeys.add(new LockedKey(key, expiration, System.currentTimeMillis(), System.currentTimeMillis(), 0, false)));

    return UUID.randomUUID().toString();
  }

  @Override
  public boolean release(final List<String> keys, final String storeId, final String token) {
    log.debug("Releasing keys {} in store {} with token {}", keys, storeId, token);
    final List<LockedKey> lockedKeys = lockMap.get(storeId);

    if (CollectionUtils.isEmpty(lockedKeys)) {
      return false;
    }

    lockedKeys.forEach(lockedKey -> {
      if (keys.contains(lockedKey.getKey())) {
        lockedKey.setReleased(true);
      }
    });
    return true;
  }

  @Override
  public boolean refresh(final List<String> keys, final String storeId, final String token, final long expiration) {
    log.debug("Refreshing keys {} in store {} with token {} to expiration: {}", keys, storeId, token, expiration);
    final List<LockedKey> lockedKeys = lockMap.get(storeId);

    if (lockedKeys == null) {
      return false;
    }

    lockedKeys.forEach(lockedKey -> {
      if (!lockedKey.isReleased()) {
        lockedKey.setExpiration(expiration);
        lockedKey.newUpdate();
      }
    });

    return true;
  }

  public List<String> getLockedKeys(final String storeId) {
    return lockMap.get(storeId).stream()
      .map(LockedKey::getKey)
      .collect(Collectors.toList());
  }

  @Data
  @AllArgsConstructor
  public static class LockedKey {
    private String key;
    private long expiration;
    private long updatedAt;
    private long createdAt;
    private long updateCounter;
    private boolean released;

    public void newUpdate() {
      this.updatedAt = System.currentTimeMillis();
      this.updateCounter++;
    }
  }
}
