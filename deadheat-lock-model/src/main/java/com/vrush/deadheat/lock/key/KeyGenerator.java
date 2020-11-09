/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.key;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Used to generate keys to lock.
 */
public interface KeyGenerator {

  /**
   * Generate keys by evaluating the given expression.
   *
   * @param lockKeyPrefix prefix to put on resolved keys
   * @param expression    key expression to evaluate
   * @param object        the root object of the expression (the object with the locked method)
   * @param method        the executed method
   * @param args          arguments with which the method was called
   * @return generated or resolved keys
   */
  List<String> resolveKeys(String lockKeyPrefix, String expression, Object object, Method method, Object[] args);
}
