/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.retry;
import com.vrush.deadheat.lock.Interval;
import com.vrush.deadheat.lock.Locked;
import com.vrush.deadheat.lock.retry.DefaultRetryTemplateConverter;
import com.vrush.deadheat.lock.retry.RetryTemplateConverter;
import com.vrush.deadheat.lock.ttl.BeanFactoryAwareIntervalConverter;
import com.vrush.deadheat.lock.ttl.IntervalConverter;
import org.junit.Test;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultRetryTemplateConverterTest {

  private final IntervalConverter intervalConverter = new BeanFactoryAwareIntervalConverter(new DefaultListableBeanFactory());

  @Test
  @Locked
  public void shouldConstructDefaultRetryTemplate() {
    final Locked locked = new Object() {}.getClass().getEnclosingMethod().getAnnotation(Locked.class);
    final RetryTemplateConverter converter = new DefaultRetryTemplateConverter(intervalConverter);
    final RetryTemplate retryTemplate = converter.construct(locked);

    assertRetryTemplateConstruction(retryTemplate, 1000L, 50000);
  }

  @Test
  @Locked(retry = @Interval("100"), timeout = @Interval("2000"))
  public void shouldConstructCustomizedRetryTemplate() {
    final Locked locked = new Object() {}.getClass().getEnclosingMethod().getAnnotation(Locked.class);
    final RetryTemplateConverter converter = new DefaultRetryTemplateConverter(intervalConverter);
    final RetryTemplate retryTemplate = converter.construct(locked);

    assertRetryTemplateConstruction(retryTemplate, 2000L, 100L);
  }

  private void assertRetryTemplateConstruction(final RetryTemplate retryTemplate, final long timeout, final long backOff) {
    final ConfigurablePropertyAccessor wrapper = PropertyAccessorFactory.forDirectFieldAccess(retryTemplate);

    assertThat(wrapper.getPropertyValue("retryPolicy")).isInstanceOf(CompositeRetryPolicy.class);
    assertThat((RetryPolicy[]) wrapper.getPropertyValue("retryPolicy.policies"))
      .hasSize(2)
      .anyMatch(policy1 -> {
        if (policy1 instanceof TimeoutRetryPolicy) {
          final TimeoutRetryPolicy timeoutRetryPolicy = (TimeoutRetryPolicy) policy1;
          assertThat(timeoutRetryPolicy.getTimeout()).isEqualTo(timeout);
          return true;
        }
        return false;
      })
      .anyMatch(policy2 -> {
        if (policy2 instanceof SimpleRetryPolicy) {
          final SimpleRetryPolicy simpleRetryPolicy = (SimpleRetryPolicy) policy2;
          assertThat(simpleRetryPolicy.getMaxAttempts()).isEqualTo(Integer.MAX_VALUE);
          return true;
        }
        return false;
      });

    assertThat(wrapper.getPropertyValue("backOffPolicy")).isInstanceOf(FixedBackOffPolicy.class);
    assertThat(((FixedBackOffPolicy) wrapper.getPropertyValue("backOffPolicy")).getBackOffPeriod()).isEqualTo(backOff);
  }
}