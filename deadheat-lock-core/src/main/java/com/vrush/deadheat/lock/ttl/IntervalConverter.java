/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.ttl;

import com.vrush.deadhead.lock.Interval;

/**
 * Converter which converts {@link Interval} to milliseconds.
 */
@FunctionalInterface
public interface IntervalConverter {

  /**
   * Convert {@link Interval} to milliseconds.
   *
   * @param interval interval to convert
   * @return milliseconds represented by the given {@code interval}
   */
  long toMillis(Interval interval);
}
