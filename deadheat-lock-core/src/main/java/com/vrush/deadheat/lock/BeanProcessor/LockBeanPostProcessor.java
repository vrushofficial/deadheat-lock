/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.beanprocessor;

import com.vrush.deadhead.lock.Locked;
import com.vrush.deadhead.lock.key.KeyGenerator;
import com.vrush.deadheat.lock.retry.RetriableLockFactory;
import com.vrush.deadheat.lock.ttl.IntervalConverter;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;

@AllArgsConstructor
public class LockBeanPostProcessor extends AbstractAdvisingBeanPostProcessor implements InitializingBean {
  private final KeyGenerator keyGenerator;
  private final LockTypeResolver lockTypeResolver;
  private final IntervalConverter intervalConverter;
  private final RetriableLockFactory retriableLockFactory;
  private final TaskScheduler taskScheduler;

  @Override
  public void afterPropertiesSet() {
    final AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, Locked.class, true);
    final Interceptor interceptor = new LockMethodInterceptor(keyGenerator, lockTypeResolver, intervalConverter, retriableLockFactory, taskScheduler);

    this.advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
  }
}
