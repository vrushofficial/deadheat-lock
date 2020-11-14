package com.vrush.deadheat.lock.retry;

import com.vrush.deadheat.lock.Lock;
import com.vrush.deadheat.lock.exception.LockNotAvailableException;
import com.vrush.deadheat.lock.retry.RetriableLock;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetriableLockTest {

  @Mock
  private Lock lock;

  @Test
  public void shouldNotRetryWhenFirstAttemptIsSuccessful() {
    when(lock.acquire(anyList(), anyString(), anyLong()))
      .thenReturn("vrush");

    final RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(new NeverRetryPolicy());

    final RetriableLock retriableLock = new RetriableLock(lock, retryTemplate);
    final String token = retriableLock.acquire(Collections.singletonList("key"), "defaultStore", 1000L);

    assertThat(token).isEqualTo("vrush");
  }

  @Test
  public void shouldRetryWhenFirstAttemptIsNotSuccessful() {
    when(lock.acquire(anyList(), anyString(), anyLong()))
      .thenReturn(null)
      .thenReturn("vrush");

    final RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2));

    final RetriableLock retriableLock = new RetriableLock(lock, retryTemplate);
    final String token = retriableLock.acquire(Collections.singletonList("key"), "defaultStore", 1000L);

    assertThat(token).isEqualTo("vrush");
    verify(lock, times(2)).acquire(anyList(), anyString(), anyLong());
  }

  @Test(expected = LockNotAvailableException.class)
  public void shouldFailRetryWhenFirstAttemptIsNotSuccessful() {
    when(lock.acquire(anyList(), anyString(), anyLong()))
      .thenReturn(null);

    final RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(new NeverRetryPolicy());

    final RetriableLock retriableLock = new RetriableLock(lock, retryTemplate);
    final String token = retriableLock.acquire(Collections.singletonList("key"), "defaultStore", 1000L);

    assertThat(token).isEqualTo("vrush");
  }
}