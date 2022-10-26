package com.gamasoft.http4kloom

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

fun testApp(request: Request) =
    Response(OK).body("Hello, ${request.query("name")}!")
        .also {
//            println(Thread.currentThread().isVirtual)

            Thread.sleep(100)//simulating some async operation without cpu load
        }

//autocannon -c 10000 -d 10 localhost:9000?name=ubi
//average 981 req/s with 100ms sleep and 100 connections
//average 1700 req/s with 100ms sleep and 10000 connections


//average 980 req/s with 100ms sleep and 100 connections
//average 80k req/s with 100ms sleep and 100 connections
