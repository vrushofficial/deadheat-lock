/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock.exception;

public class DistributedLockException extends RuntimeException {
  public DistributedLockException(final String message) {
    super(message);
  }

  public DistributedLockException(final String msg, final Throwable e) {
    super(msg, e);
  }
}