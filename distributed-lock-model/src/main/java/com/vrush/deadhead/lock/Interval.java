/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
public @interface Interval {

  /**
   * Interval period.
   * By default, can be specified as 'property placeholder', e.g. {@code ${locked.interval}}.
   */
  String value();

  /**
   * Interval {@link TimeUnit} represented by {@link #value()}.
   */
  TimeUnit unit() default TimeUnit.MILLISECONDS;
}
