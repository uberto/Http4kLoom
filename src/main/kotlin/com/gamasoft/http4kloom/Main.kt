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


//autocannon -c XXX -d 20 localhost:9000?name=ubi

//normal jetty
//average 80k req/s with no sleep and 100 connections (top 200% CPU)
//average 94k req/s with no sleep and 10000 connections (top 200% CPU)
//average 1970 req/s with 50ms sleep and 100 connections (top 25% CPU)
//average 3600 req/s with 50ms sleep and 10000 connections (top 80% CPU)

//500T
//average 80k req/s with no sleep and 100 connections (top 200% CPU)
//average 94k req/s with no sleep and 10000 connections (top 200% CPU)
//average 1970 req/s with 50ms sleep and 100 connections (top 35% CPU)
//average 9500 req/s with 50ms sleep and 10000 connections (top 130% CPU)

//loom
//average 77k req/s with no sleep and 100 connections (top 250% CPU)
//average 94k req/s with no sleep and 10000 connections (top 250% CPU)
//average 1970 req/s with 50ms sleep and 100 connections (top 40% CPU)
//average 94k req/s with 50ms sleep and 10000 connections (top 400% CPU)
