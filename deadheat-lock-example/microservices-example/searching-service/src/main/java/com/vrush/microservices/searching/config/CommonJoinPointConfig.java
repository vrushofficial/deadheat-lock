package com.vrush.microservices.searching.config;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {

	@Pointcut("@annotation(com.vrush.microservices.searching.annotations.TrackMethod)")
	public void trackMethod(){}

}
