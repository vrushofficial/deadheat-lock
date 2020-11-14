/**
 * @author Vrushabh Joshi
 *
 */

package com.vrush.deadheat.lock.processor;

import com.vrush.deadheat.lock.Interval;
import com.vrush.deadheat.lock.Locked;
import com.vrush.deadheat.lock.processor.support.SimpleLock;
import com.vrush.deadheat.lock.processor.support.SimpleLocked;

interface LockedInterface {

  @Locked(prefix = "lock:", expression = "#s", type = SimpleLock.class)
  void doLocked(int num, String s);

  @SimpleLocked(expression = "#s")
  void doLockedWithAlias(int num, String s);

  void doLockedFromImplementation(int num, String s);

  @SimpleLocked
  void doLockedWithExecutionPath();

  @SimpleLocked(refresh = @Interval("100"), expiration = @Interval("200"))
  void sleep() throws InterruptedException;
}