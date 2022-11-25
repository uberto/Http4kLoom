import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.server.asServer
import com.gamasoft.http4kloom.testApp
import org.eclipse.jetty.util.thread.ExecutorThreadPool
import org.http4k.server.Jetty

fun main() {
//    val server = ::testApp.asServer(Jetty(9000)).start() //normal jetty
//    val server = ::testApp.asServer(JettyLoom(9000, ExecutorThreadPool(500))).start() //fixed threads
    val server = ::testApp.asServer(JettyLoom(9000, LoomThreadPool())).start() //loom

    val client = ApacheClient()

    val request = Request(Method.GET, "http://localhost:9000").query("name", "John Doe")

    println(client(request))

    val times = 100
    repeat(times) {
        println(times - it)
        Thread.sleep(1000)
    }
    server.stop()
}


//autocannon -c 10000 -d 10 localhost:9000?name=ubi
//average 981 req/s with 100ms sleep and 100 connections
//average 1700 req/s with 100ms sleep and 10000 connections


//average 980 req/s with 100ms sleep and 100 connections
//average 80k req/s with 100ms sleep and 100 connections
