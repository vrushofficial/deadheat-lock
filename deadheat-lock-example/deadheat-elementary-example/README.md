# DeadHeat Lock Example

Example and simple usecase of DeadHeat lock with Spring Boot.

A Controller having an endpoint,

```/hello/{name}```

Annotated service implementation with `@RedisDeadHeatSingleLock(expression = "#name")` (SpEL expression is used to withdraw values) which ensures method cannot be run in parallel from multiple JVMs.

Where lock is acquiring on "name" (path variable) as a key with default living time of 10 secs.

To manually test this locking mechanism you can run multiple instances of this application by multiple ports in `application.properties`,

1) Hit an endpoint with some name on first app
2) Pause first application before releasing acquired lock(debug-level)
3) Hit an endpoint with same name on second app - you'll get an exception as lock is already there with same key. 

 

 


