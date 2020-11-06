/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.exception;

import com.vrush.deadhead.lock.exception.DistributedLockException;

public class LockNotAvailableException extends DistributedLockException {
  public LockNotAvailableException(final String msg) {
    super(msg);
  }
}
