package com.vrush.microservices.financial.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {
	
	@Pointcut("@annotation(com.vrush.microservices.financial.annotations.TrackMethod)")
	public void trackMethod(){}

}
