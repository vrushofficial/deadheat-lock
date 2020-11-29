package com.vrush.example.deadheatelementaryexample.service;

import com.vrush.deadhead.lock.redis.annotation.RedisDeadHeatSingleLock;

/**
 * modified by @author Vrushabh Joshi last on 28-11-2020 10:28
 */
public class LockedHelloService implements HelloService {

  @Override
  @RedisDeadHeatSingleLock(expression = "#name")
  public String sayHello(final String name) {
    return "Hello " + name + "!";
  }
}