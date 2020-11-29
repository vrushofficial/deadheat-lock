# DeadHeat Lock
> "No matter how much we might wish it, there is no way to build a lock that only holy being can open and demons cannot".
A wise like to remind us. 

“Anyone who’s trying to sell you a lock is selling you vain and lies.”
This may sound rather bleak, but it doesn’t say that locking itself is impossible in a distributed system: it’s just that all of the system’s components must participate in the protocol.

This region(DeadHeat Lock) deal with coarse-grained synchronization within systems, and
in particular to deal with the problem of electing a leader from among a set of otherwise equivalent servers and cease others in a queue.

### Let's look at the Locking state of affairs first
Say you have an application in which a client A1 needs to update a file in shared storage (e.g. HDFS or S3) in transactional manner.
And at the same time a client A2 wants to perform same operations on same data.  

Then what could be the result? Ans: It might be inconsistency, data losts or major performance problems.  
So, leaving critical sections unprotected could break the correctness of the system and produce critical business errors.

### Protecting a resource with a lock(Solution)
A client first acquires the lock, then reads the file, 
makes some changes, writes the modified file back, and finally releases the lock- A straightforward approch.  
DeadHeat lock prevents two clients from performing this read-modify-write cycle concurrently.Well, it's not as simple as it sounds.

In that case, code might something look like such case,

```function writeData(filename, data) {
    var lock = lockService.acquireLock(filename);
    if (!lock) {
        throw 'Failed to acquire lock';
    }
    try {
        var file = storage.readFile(filename);
        var updated = updateContents(file, data);
        storage.writeFile(filename, updated);
    } finally {
        lock.release();
    }
}
```
Unfortunately, even if you have a perfect lock service, the code above is broken. The following diagram shows how you can end up with corrupted data:
In this example, the client that acquired the lock is paused for an extended period of time while holding the lock
– for example because the garbage collector (GC) kicked in. 
The lock has a ttl (i.e. it is a lease), which is always a good idea (otherwise a crashed client could end up holding a lock forever and never releasing it). 
However, if the GC pause lasts longer than the lease expiry period, and the client doesn’t realise that it has expired, it may go ahead and make some unsafe change.

![unsafe-lock.png](unsafe-lock.png)

## Solution : Make Lock safe with fencing   
(Which "Redis" does not supports directly)

The fix for this problem is actually pretty simple: you need to include a fencing token with every write request to the storage service. In this context, a fencing token is simply a number that increases (e.g. incremented by the lock service) every time a client acquires the lock. This is illustrated in the following diagram:

![fencing-tokens.png](fencing-tokens.png)


DeadHeat Lock is re-implement Redisson logic by own, to avoid any additional frameworks in our tech stack. The most important part for me is to emphasize how easy it is to implement locking on your own and what additional benefits we could gain in monitoring and troubleshooting concurrent access.

## Features by DeadHeat locks
#### Mutual exclusion
Only one client can hold a lock at a given moment.

#### Deadlock free
Distributed locks use a lease-based locking mechanism. If a client acquires a lock and encounters an exception, the lock is automatically released after a certain period. This prevents resource deadlocks.

#### Wait-Free Code
Code will be wait-free and any given task doesn’t have to wait until the lock will be released and just exits early detecting concurrent execution

#### Manually Control
DeadHeat Lock allows users completly handle ttl, expirations, tokens, retrier etc.

## Importing into project
#### Maven
```
<dependency>
  <groupId>com.github.vrushofficial.deadheat-lock</groupId>
  <artifactId>deadheat-lock-redis</artifactId>
  <version>1.0.0</version>
</dependency>
```
#### Gradle
```implementation 'com.github.vrushofficial.deadheat-lock:deadheat-lock-redis:1.0.0'```

#### Kotlin
```implementation("com.github.vrushofficial.deadheat-lock:deadheat-lock-redis:1.0.0")```

#### Scala 
```libraryDependencies += "com.github.vrushofficial.deadheat-lock" % "deadheat-lock-redis" % "1.0.0"```

#### Apache Lvy
```<dependency org="com.github.vrushofficial.deadheat-lock" name="deadheat-lock-redis" rev="1.0.0" />```

#### Groovy
```@Grapes(
  @Grab(group='com.github.vrushofficial.deadheat-lock', module='deadheat-lock-redis', version='1.0.0')
)
```

#### Leiningen
```[com.github.vrushofficial.deadheat-lock/deadheat-lock-redis "1.0.0"]```


## Simple Usage
All you have to do  is,  
add `@RedisDeadHeatLock` at main class      
add  `@RedisDeadHeatSingleLock` or `@RedisDeadHeatMultiLock` as per your convenient with config.    
(Sounds quite simple to those who are writing redis scripts)


With addition, Spring BeanPostProcessor will handle all `@Locked` methods including their aliases. The type field describes which implementation of the lock to use. To prevent repeating yourself if
 you
 plan on using the same implementation (as most usually will), I’ve added alias support. They wrap the `@Locked` annotation and define the type used.
Each lock needs to define a `SpEL` expression used to acquire the lock.
Locks can be refreshed automatically on a regular interval. This allows methods that occasionally need to run longer than their expiration. Refreshing the lock periodically prolongs the expiration of its key(s).

### Examples

Locking with multiple keys determined in runtime, use SpEL, for an example:
``` 
@RedisDeadHeatMultiLock(expression = "T(com.github.vrushofficial).getNamesWithId(#p0)")
 public void runLockedWithRedis(final int id) {
     // locked code
 }
```
This means that the runLockedWithRedis method will execute only if all keys evaluated by expression were acquired.

Locking with single determined in runtime:
``` 
@RedisDeadHeatSingleLock
 public Booking save(@NonNull final BookingPostDTO bookingDTO, @NonNull final String key) {
    //locked code
 }
```
Locking with a custom lock implementation based on value of integer field count:
```
@Locked(type = MyCustomLock.class, expression = "getCount", prefix = "using:")
public void runLockedWithMyCustomLock() {
    // locked code
}
```
Manually controlled locks.  
For more grained control (e.g., locking in the middle of the method and releasing later in the code), inject the lock in your service and acquire the lock manually:
```
public class Example {
   
       @Qualifier("simpleRedisLock")
       private Lock lock;
   
       // other fields...
   
       private void manuallyLocked() {
           // code before locking...
   
           final String token = lock.acquire(keys, storeId, expiration, retry, timeout);
   
           // check if you acquired a token
           if (StringUtils.isEmpty(token)) {
               throw new IllegalStateException("Lock not acquired!");
           }
   
           // code after locking...
   
           lock.release(keys, token, storeId);
   
           // code after releasing the lock...
       }
   }
```
#### SpEL key generator
This is the default key generator the advice uses. If you wish to use your own, simply write your own and define it as a `@Bean`.  
The default key generator has access to the currently executing context, meaning you can access your fields and methods from SpEL. It uses the `DefaultParameterNameDiscoverer` to discover parameter
 names, so you can access your parameters in several different ways:   
 1) using `p#` syntax, where `#` is the position of the parameter, for an example: `p0` for the first parameter

2) using `a#` syntax, where `#` is the position of the parameter, for an example: `a2` for the third parameter

3) using the parameter name, for an example, `#message — REQUIRES -parameters` compiler flag

A special variable named `executionPath` is used to define the method called. This is the default expression used to describe the annotated method.

All validated expressions that result in an Iterable or an array will be converted to `List<String>` and all other values will be wrapped with `Collections.singletonList.Elements` of Iterable or
 array will also be converted to Strings using the ConversionService. Custom converters can be registered. More about Spring conversion can be found here.

For more examples, take a look at `com.vrush.deadheat.lock.key.SpelKeyGeneratorTest`.



#### Constantly striving to improve and love to hear what you think. Your feedback will help determine features to add and how we can make the product better for you.

    














