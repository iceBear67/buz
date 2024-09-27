# buz
A lightweight eventbus with class hierarchy and thread scheduling support.

# Benchmark

[BenchmarkEventbus](./src/jmh/java/buz/BenchmarkEventbus.java)  
Running on ArchLinux(6.10.10-zen1-1-zen). CPU: AMD Ryzen 7 4800U with Radeon Graphics (16) @ 1.800GHz, 16GB ram in total.  
With 1000 empty listeners registered. (in MultipleType mode, 500 listeners for both types). MultiThreadedEventBus is not tested, as it is just based on what we benchmark (HierarchyTypedEventBus)

Oracle GraalVM 21
```
# JMH version: 1.36
# VM version: JDK 21.0.4, Java HotSpot(TM) 64-Bit Server VM, 21.0.4+8-LTS-jvmci-23.1-b41
# VM invoker: /home/icybear/.sdkman/candidates/java/21.0.4-graal/bin/java
# VM options: -XX:ThreadPriorityPolicy=1 -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCIProduct -XX:-UnlockExperimentalVMOptions -Dfile.encoding=UTF-8 -Djava.io.tmpdir=/home/icybear/IdeaProjects/buz/build/tmp/jmh -Duser.country=CN -Duser.language=zh -Duser.variant
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 2 iterations, 10 s each
# Measurement: 2 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: buz.BenchmarkEventbus.benchmarkSingleType


Benchmark                                      Mode  Cnt       Score   Error  Units
BenchmarkEventbus.benchmarkMultipleType       thrpt    2  696291.091          ops/s
BenchmarkEventbus.benchmarkMultipleType:·jfr  thrpt              NaN            ---
BenchmarkEventbus.benchmarkSingleType         thrpt    2  722170.866          ops/s
BenchmarkEventbus.benchmarkSingleType:·jfr    thrpt              NaN            ---

```