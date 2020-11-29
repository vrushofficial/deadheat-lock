package com.vrush.microservices.consumer.config;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {
	
	@Pointcut("@annotation(com.vrush.microservices.consumer.annotations.TrackMethod)")
	public void trackMethod(){}

}
