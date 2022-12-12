package com.gamasoft.http4kloom

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

fun testApp(request: Request) =
    Response(OK).body("Hello, ${request.query("name")}!")
        .also {
//            println(Thread.currentThread().isVirtual)

//            Thread.sleep(1)//simulating some async operation without cpu load
        }

