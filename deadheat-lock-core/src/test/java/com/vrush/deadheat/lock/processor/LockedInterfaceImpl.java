/**
 * @author Vrushabh Joshi
 *
 */

package com.vrush.deadheat.lock.processor;

import com.vrush.deadheat.lock.Locked;
import com.vrush.deadheat.lock.processor.support.SimpleLock;
import com.vrush.deadheat.lock.processor.support.SimpleLocked;
import java.util.concurrent.TimeUnit;

class LockedInterfaceImpl implements LockedInterface {

  @Override
  public void doLocked(final int num, final String s) {
  }

  @Override
  public void doLockedWithAlias(final int num, final String s) {
  }

  @Override
  @Locked(prefix = "lock:", expression = "#s", type = SimpleLock.class)
  public void doLockedFromImplementation(final int num, final String s) {
  }

  @Override
  public void doLockedWithExecutionPath() {
  }

  @Override
  public void sleep() throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
  }

  public int getStaticValue() {
    return 4;
  }
}
