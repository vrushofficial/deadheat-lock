/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.retry;

import com.vrush.deadhead.lock.Locked;
import org.springframework.retry.support.RetryTemplate;

/**
 * Converter which constructs {@link RetryTemplate} based on properties defined in {@link Locked} annotations.
 */
@FunctionalInterface
public interface RetryTemplateConverter {

  /**
   * Construct a {@link RetryTemplate} which retries executions as defined by properties of the {@link Locked} annotation.
   *
   * @param locked locked method annotation
   * @return constructed {@link RetryTemplate}
   */
  RetryTemplate construct(Locked locked);
}
