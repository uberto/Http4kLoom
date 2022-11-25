# Http4kLoom
Super simple test to check Http4k with Loom

## How to start

You need a Jvm19 or later.
Either configure IntelliJ to run on language level 19-preview and run Main or use gradle from command line:

```./gradlew run```

and in another terminal launch some performance tool like autocannon:

```autocannon -c 10000 -d 10 localhost:9000?name=ubi```

## Results

Vanilla Http4k-jetty:

Average 981 req/s with 100ms sleep and 100 connections
Average 1700 req/s with 100ms sleep and 10000 connections

Loom enabled Http4k-jetty:

Average 980 req/s with 100ms sleep and 100 connections
Average 80k req/s with 100ms sleep and 10000 connections

So 40x performance increase... not bad.

