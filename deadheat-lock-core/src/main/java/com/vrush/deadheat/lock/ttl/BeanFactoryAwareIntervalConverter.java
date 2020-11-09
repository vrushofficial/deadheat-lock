/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.ttl;

import com.vrush.deadheat.lock.Interval;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.StringUtils;

/**
 * {@link IntervalConverter} capable of resolving properties.
 */
@AllArgsConstructor
public class BeanFactoryAwareIntervalConverter implements IntervalConverter {
  private final ConfigurableBeanFactory beanFactory;

  @Override
  public long toMillis(final Interval interval) {
    return convertToMilliseconds(interval, resolveMilliseconds(interval));
  }

  private String resolveMilliseconds(final Interval interval) {
    final String value = beanFactory.resolveEmbeddedValue(interval.value());
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException("Cannot convert interval " + interval + " to milliseconds");
    }
    return value;
  }

  private long convertToMilliseconds(final Interval interval, final String value) {
    try {
      return interval.unit().toMillis(Long.parseLong(value));
    } catch (final NumberFormatException e) {
      throw new IllegalArgumentException("Cannot convert interval " + interval + " to milliseconds", e);
    }
  }
}
