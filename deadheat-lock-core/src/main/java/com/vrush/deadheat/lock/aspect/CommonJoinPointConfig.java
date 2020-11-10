package com.vrush.deadheat.lock.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {
	
	@Pointcut("@annotation(com.vrush.deadheat.lock.aspect.TrackMethod)")
	public void trackMethod(){}

}
