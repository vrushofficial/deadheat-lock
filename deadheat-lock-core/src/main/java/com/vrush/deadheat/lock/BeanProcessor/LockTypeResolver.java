/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.beanprocessor;

import com.vrush.deadheat.lock.Lock;

@FunctionalInterface
public interface LockTypeResolver {

  /**
   * Get a {@link Lock} for the given {@code type}.
   *
   * @param type type of the lock
   * @return lock of the given {@code type}
   */
  Lock get(Class<? extends Lock> type);
}
