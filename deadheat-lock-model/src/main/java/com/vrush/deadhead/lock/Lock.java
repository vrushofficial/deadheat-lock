/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadhead.lock;

import java.util.List;

public interface Lock {

  /**
   * Try to acquire the lock.
   *
   * @param keys       keys to try to lock
   * @param storeId    lock store id to save keys in (table, collection, ...)
   * @param expiration how long to wait before releasing the key automatically, in millis
   * @return token to use for releasing the lock or {@code null} if lock cannot be acquired at the moment
   */
  String acquire(List<String> keys, String storeId, long expiration);

  /**
   * Try to release the lock if token held by the lock has not changed.
   *
   * @param keys    keys to try to unlock
   * @param storeId lock store id to release keys in (table, collection, ...)
   * @param token   token used to check if lock is still held by this lock
   * @return {@code true} if lock was successfully released, {@code false} otherwise
   */
  boolean release(List<String> keys, String storeId, String token);

  /**
   * Try to refresh the lock expiration.
   *
   * @param keys       keys to try to refresh
   * @param storeId    lock store id to refresh keys in (table, collection, ...)
   * @param expiration how long to wait before releasing the key automatically, in millis
   * @param token      token used to check if lock is still held by this lock
   * @return {@code true} if lock was successfully refreshed, {@code false} otherwise
   */
  boolean refresh(List<String> keys, String storeId, String token, long expiration);
}
