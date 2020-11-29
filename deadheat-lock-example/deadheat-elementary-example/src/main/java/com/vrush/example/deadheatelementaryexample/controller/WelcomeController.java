package com.vrush.example.deadheatelementaryexample.controller;

import com.vrush.example.deadheatelementaryexample.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * modified by @author Vrushabh Joshi last on 28-11-2020 10:18
 */
@RestController
public class WelcomeController {

  private final HelloService helloService;

  @Autowired
  public WelcomeController(final HelloService helloService) {
    this.helloService = helloService;
  }

  @GetMapping("/hello/{name}")
  public String sayHello(@PathVariable("name") final String name) {
    return helloService.sayHello(name);
  }
}