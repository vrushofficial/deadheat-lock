/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.exception;

import com.vrush.deadhead.lock.exception.DistributedLockException;

public class EvaluationConvertException extends DistributedLockException {
  public EvaluationConvertException(final String msg) {
    super(msg);
  }
}
