/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.configuration;

import com.vrush.deadhead.lock.key.KeyGenerator;
import com.vrush.deadheat.lock.beanprocessor.LockBeanPostProcessor;
import com.vrush.deadheat.lock.beanprocessor.LockTypeResolver;
import com.vrush.deadheat.lock.keystore.SpelKeyGenerator;
import com.vrush.deadheat.lock.retry.DefaultRetriableLockFactory;
import com.vrush.deadheat.lock.retry.DefaultRetryTemplateConverter;
import com.vrush.deadheat.lock.retry.RetriableLockFactory;
import com.vrush.deadheat.lock.ttl.BeanFactoryAwareIntervalConverter;
import com.vrush.deadheat.lock.ttl.IntervalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class DistributedLockConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public LockBeanPostProcessor lockBeanPostProcessor(final KeyGenerator keyGenerator,
                                                     final ConfigurableBeanFactory configurableBeanFactory,
                                                     final IntervalConverter intervalConverter,
                                                     final RetriableLockFactory retriableLockFactory,
                                                     @Autowired(required = false) final TaskScheduler taskScheduler) {
    final LockBeanPostProcessor processor = new LockBeanPostProcessor(keyGenerator, configurableBeanFactory::getBean, intervalConverter, retriableLockFactory, taskScheduler);
    processor.setBeforeExistingAdvisors(true);
    return processor;
  }

  @Bean
  @ConditionalOnMissingBean
  public IntervalConverter intervalConverter(final ConfigurableBeanFactory configurableBeanFactory) {
    return new BeanFactoryAwareIntervalConverter(configurableBeanFactory);
  }

  @Bean
  @ConditionalOnMissingBean
  public RetriableLockFactory retriableLockFactory(final IntervalConverter intervalConverter) {
    return new DefaultRetriableLockFactory(new DefaultRetryTemplateConverter(intervalConverter));
  }

  @Bean
  @ConditionalOnMissingBean
  public KeyGenerator spelKeyGenerator(final ConversionService conversionService) {
    return new SpelKeyGenerator(conversionService);
  }

  @Bean
  @ConditionalOnMissingBean
  public LockTypeResolver lockTypeResolver(final ConfigurableBeanFactory configurableBeanFactory) {
    return configurableBeanFactory::getBean;
  }

  @Bean
  @ConditionalOnMissingBean
  public ConversionService conversionService() {
    return new DefaultConversionService();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "com.vrush.deadheat.lock.task-scheduler.default", name = "enabled", havingValue = "true", matchIfMissing = true)
  public TaskScheduler taskScheduler() {
    return new ThreadPoolTaskScheduler();
  }
}
