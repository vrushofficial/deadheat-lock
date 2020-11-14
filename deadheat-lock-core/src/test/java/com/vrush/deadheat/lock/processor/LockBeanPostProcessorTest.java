/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.processor;

import com.vrush.deadheat.lock.Interval;
import com.vrush.deadheat.lock.Locked;
import com.vrush.deadheat.lock.beanprocessor.LockBeanPostProcessor;
import com.vrush.deadheat.lock.beanprocessor.LockTypeResolver;
import com.vrush.deadheat.lock.keystore.SpelKeyGenerator;
import com.vrush.deadheat.lock.processor.support.SimpleLock;
import com.vrush.deadheat.lock.processor.support.SimpleLock.LockedKey;
import com.vrush.deadheat.lock.processor.support.SimpleLocked;
import com.vrush.deadheat.lock.retry.DefaultRetriableLockFactory;
import com.vrush.deadheat.lock.retry.DefaultRetryTemplateConverter;
import com.vrush.deadheat.lock.retry.RetriableLockFactory;
import com.vrush.deadheat.lock.ttl.BeanFactoryAwareIntervalConverter;
import com.vrush.deadheat.lock.ttl.IntervalConverter;
import java.util.concurrent.TimeUnit;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class LockBeanPostProcessorTest {
  private LockedInterface lockedInterface;
  private SimpleLock lock;

  @Before
  public void setUp() {
    final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    lock = new SimpleLock();

    final LockTypeResolver lockTypeResolver = Mockito.mock(LockTypeResolver.class);
    when(lockTypeResolver.get(SimpleLock.class)).thenReturn(lock);

    final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.afterPropertiesSet();

    final SpelKeyGenerator keyGenerator = new SpelKeyGenerator(new DefaultConversionService());
    final IntervalConverter intervalConverter = new BeanFactoryAwareIntervalConverter(beanFactory);
    final RetriableLockFactory retriableLockFactory = new DefaultRetriableLockFactory(new DefaultRetryTemplateConverter(intervalConverter));

    final LockBeanPostProcessor processor = new LockBeanPostProcessor(keyGenerator, lockTypeResolver, intervalConverter, retriableLockFactory, scheduler);
    processor.afterPropertiesSet();

    beanFactory.addBeanPostProcessor(processor);
    beanFactory.registerBeanDefinition("lockedService", new RootBeanDefinition(LockedInterface.class, LockedInterfaceImpl::new));
    lockedInterface = beanFactory.getBean(LockedInterface.class);
    beanFactory.preInstantiateSingletons();
  }

  @Before
  public void cleanLockState() {
    lock.getLockMap().clear();
  }

  @Test
  public void shouldLockInheritedFromInterface() {
    lockedInterface.doLocked(1, "vrush");
    assertThat(lock.getLockedKeys("lock")).containsExactly("lock:vrush");
  }

  @Test
  public void shouldLockFromImplementation() {
    lockedInterface.doLockedFromImplementation(1, "vrush");
    assertThat(lock.getLockedKeys("lock")).containsExactly("lock:vrush");
  }

  @Test
  public void shouldLockWithExecutionPath() {
    lockedInterface.doLockedWithExecutionPath();
    assertThat(lock.getLockedKeys("lock")).containsExactly("com.vrush.deadheat.lock.processor.LockedInterfaceImpl.doLockedWithExecutionPath");
  }

 @Test
  public void shouldRefreshLock() throws InterruptedException {
    lockedInterface.sleep();
    final LockedKey lockedKey = this.lock.getLockMap().get("lock").get(0);
    assertThat(lockedKey.getUpdatedAt()).withFailMessage(lockedKey.toString()).isCloseTo(System.currentTimeMillis(), Offset.offset(200L));
    assertThat(lockedKey.getKey()).isEqualTo("com.vrush.deadheat.lock.processor.LockedInterfaceImpl.sleep");

    assertThat(lockedKey.getUpdateCounter()).isIn(10L, 11L);
    lock.getLockMap().clear();
  }
}