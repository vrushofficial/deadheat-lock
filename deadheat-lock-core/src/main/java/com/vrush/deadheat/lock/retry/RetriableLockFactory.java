/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.retry;

import com.vrush.deadhead.lock.Lock;
import com.vrush.deadhead.lock.Locked;

public interface RetriableLockFactory {

  /**
   * Generates a {@link RetriableLock} that will retry {@code lock} token acquisition logic as specified by {@code locked}.
   *
   * @param lock   lock which {@link Lock#acquire} logic should be retried
   * @param locked annotation describing how to retry
   * @return a retriable lock version of {@code lock}
   */
  RetriableLock generate(Lock lock, Locked locked);
}
