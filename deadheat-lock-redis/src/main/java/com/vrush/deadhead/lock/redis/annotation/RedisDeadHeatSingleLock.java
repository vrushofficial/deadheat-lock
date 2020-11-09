/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock.redis.annotation;

import com.vrush.deadhead.lock.redis.impl.SimpleRedisLock;
import com.vrush.deadheat.lock.Interval;
import com.vrush.deadheat.lock.Locked;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Locked(type = SimpleRedisLock.class)
public @interface RedisDeadHeatSingleLock {

  boolean manuallyReleased() default false;

  String storeId() default "lock";

  String prefix() default "";

  String expression() default "#executionPath";

  Interval expiration() default @Interval(value = "10", unit = TimeUnit.SECONDS);

  Interval timeout() default @Interval(value = "1", unit = TimeUnit.SECONDS);

  Interval retry() default @Interval(value = "50");

  Interval refresh() default @Interval(value = "0");
}
