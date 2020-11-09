/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock.redis.configuration;

import com.vrush.deadheat.lock.configuration.DistributedLockConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DistributedLockConfiguration.class, RedisDeadHeatLockConfiguration.class})
public @interface RedisDeadHeatLock {
}
