/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.retry;

import com.vrush.deadheat.lock.Locked;
import com.vrush.deadheat.lock.exception.LockNotAvailableException;
import com.vrush.deadheat.lock.ttl.IntervalConverter;
import java.util.Collections;
import lombok.Data;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Data
public class DefaultRetryTemplateConverter implements RetryTemplateConverter {
  private final IntervalConverter intervalConverter;

  @Override
  public RetryTemplate construct(final Locked locked) {
    final RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(resolveLockRetryPolicy(locked));
    retryTemplate.setBackOffPolicy(resolveBackOffPolicy(locked));
    return retryTemplate;
  }

  private CompositeRetryPolicy resolveLockRetryPolicy(final Locked locked) {
    final CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();

    final RetryPolicy timeoutRetryPolicy = resolveTimeoutRetryPolicy(locked);
    final RetryPolicy exceptionTypeRetryPolicy = resolveExceptionTypeRetryPolicy();

    compositeRetryPolicy.setPolicies(new RetryPolicy[]{timeoutRetryPolicy, exceptionTypeRetryPolicy});
    return compositeRetryPolicy;
  }

  private RetryPolicy resolveTimeoutRetryPolicy(final Locked locked) {
    final TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
    timeoutRetryPolicy.setTimeout(intervalConverter.toMillis(locked.timeout()));
    return timeoutRetryPolicy;
  }

  private RetryPolicy resolveExceptionTypeRetryPolicy() {
    return new SimpleRetryPolicy(Integer.MAX_VALUE, Collections.singletonMap(LockNotAvailableException.class, true));
  }

  private FixedBackOffPolicy resolveBackOffPolicy(final Locked locked) {
    final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(intervalConverter.toMillis(locked.retry()));
    return fixedBackOffPolicy;
  }
}
