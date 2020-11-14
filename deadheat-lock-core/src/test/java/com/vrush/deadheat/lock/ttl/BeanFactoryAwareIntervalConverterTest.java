/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.ttl;

import com.vrush.deadheat.lock.Interval;
import com.vrush.deadheat.lock.ttl.BeanFactoryAwareIntervalConverter;
import com.vrush.deadheat.lock.ttl.IntervalConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "locked.interval=10")
public class BeanFactoryAwareIntervalConverterTest {

  @Autowired
  private IntervalConverter intervalConverter;

  @Test
  @Interval(value = "${locked.interval}")
  public void shouldResolveProperty() {
    assertThat(intervalConverter.toMillis(new Object() {}.getClass().getEnclosingMethod().getAnnotation(Interval.class)))
      .isEqualTo(10);
  }

  @SpringBootApplication
  public static class BeanFactoryAwareIntervalConverterTestApplication {

    @Bean
    public IntervalConverter intervalConverter(final ConfigurableListableBeanFactory configurableListableBeanFactory) {
      return new BeanFactoryAwareIntervalConverter(configurableListableBeanFactory);
    }
  }
}