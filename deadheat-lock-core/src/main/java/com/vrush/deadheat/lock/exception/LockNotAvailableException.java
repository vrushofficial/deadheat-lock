/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.exception;

public class LockNotAvailableException extends DistributedLockException {
  public LockNotAvailableException(final String msg) {
    super(msg);
  }
}
