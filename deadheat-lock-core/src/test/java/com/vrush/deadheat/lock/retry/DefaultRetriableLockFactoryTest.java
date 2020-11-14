/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.retry;

import com.vrush.deadheat.lock.Lock;
import com.vrush.deadheat.lock.Locked;
import com.vrush.deadheat.lock.retry.DefaultRetriableLockFactory;
import com.vrush.deadheat.lock.retry.RetriableLock;
import com.vrush.deadheat.lock.retry.RetriableLockFactory;
import com.vrush.deadheat.lock.retry.RetryTemplateConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRetriableLockFactoryTest {

  @Mock
  private RetryTemplateConverter retryTemplateConverter;

  @Mock
  private Lock lock;

  @Mock
  private Locked locked;

  @Mock
  private RetryTemplate retryTemplate;

  @Test
  public void shouldGenerateRetriableLock() {
    when(retryTemplateConverter.construct(eq(locked))).thenReturn(retryTemplate);

    final RetriableLockFactory factory = new DefaultRetriableLockFactory(retryTemplateConverter);
    final RetriableLock retriableLock = factory.generate(lock, locked);

    assertThat(retriableLock.getLock()).isEqualTo(lock);
    assertThat(retriableLock.getRetryTemplate()).isEqualTo(retryTemplate);
  }
}