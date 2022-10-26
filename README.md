# Http4kLoom
Super simple test to check Http4k with Loom


Since current IntelliJ(2022.2.3) doesn't compile for me I measured it launching in a terminal

```./gradlew run```

and in anouther terminal

```autocannon -c 10000 -d 10 localhost:9000?name=ubi```


Vanilla Http4k-jetty:

Average 981 req/s with 100ms sleep and 100 connections
Average 1700 req/s with 100ms sleep and 10000 connections

Loom enabled Http4k-jetty:

Average 980 req/s with 100ms sleep and 100 connections
Average 80k req/s with 100ms sleep and 10000 connections

So 40x performance increase... not bad.

