/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.retry;

import com.vrush.deadheat.lock.Lock;
import com.vrush.deadheat.lock.aspect.TrackMethod;
import com.vrush.deadheat.lock.exception.LockNotAvailableException;
import java.util.List;
import lombok.Data;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

/**
 * A {@link Lock} wrapper for retrying {@link #acquire} method calls. This wrapper will retry the acquire method
 * only as specified by the provided {@link RetryTemplate}.
 */
@Data
public class RetriableLock implements Lock {
  private final Lock lock;
  private final RetryTemplate retryTemplate;

  @TrackMethod
  @Override
  public String acquire(final List<String> keys, final String storeId, final long expiration) {
    return retryTemplate.execute(ctx -> {
      final String token = lock.acquire(keys, storeId, expiration);

      if (StringUtils.isEmpty(token)) {
        throw new LockNotAvailableException(String.format("Lock not available for keys: %s in store %s", keys, storeId));
      }

      return token;
    });
  }

  @TrackMethod
  @Override
  public boolean release(final List<String> keys, final String storeId, final String token) {
    return lock.release(keys, storeId, token);
  }

  @TrackMethod
  @Override
  public boolean refresh(final List<String> keys, final String storeId, final String token, final long expiration) {
    return lock.refresh(keys, storeId, token, expiration);
  }
}
