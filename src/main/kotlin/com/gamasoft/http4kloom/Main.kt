import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.server.asServer
import com.gamasoft.http4kloom.testApp
import org.http4k.server.Jetty

fun main() {
    val server = ::testApp.asServer(JettyLoom(9000)).start()
//    val server = ::testApp.asServer(Jetty(9000)).start()

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