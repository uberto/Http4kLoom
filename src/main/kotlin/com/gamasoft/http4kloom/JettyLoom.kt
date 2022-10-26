import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.HandlerWrapper
import org.eclipse.jetty.server.handler.StatisticsHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.thread.ThreadPool
import org.eclipse.jetty.websocket.core.FrameHandler
import org.eclipse.jetty.websocket.core.WebSocketComponents
import org.eclipse.jetty.websocket.core.server.WebSocketNegotiation
import org.eclipse.jetty.websocket.core.server.WebSocketNegotiator
import org.eclipse.jetty.websocket.core.server.WebSocketUpgradeHandler
import org.http4k.core.HttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Http4kWebSocketFrameHandler
import org.http4k.server.PolyServerConfig
import org.http4k.server.ServerConfig
import org.http4k.servlet.jakarta.asHttp4kRequest
import org.http4k.servlet.jakarta.asServlet
import org.http4k.sse.SseHandler
import org.http4k.websocket.WsHandler
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class JettyLoom(private val port: Int, override val stopMode: ServerConfig.StopMode, private val server: Server) :
    PolyServerConfig {
    constructor(port: Int = 8000) : this(port, ServerConfig.StopMode.Graceful(Duration.ofSeconds(5)))
    constructor(port: Int = 8000, stopMode: ServerConfig.StopMode) : this(port, stopMode, http(port))
    constructor(port: Int = 8000, server: Server) : this(port, ServerConfig.StopMode.Graceful(Duration.ofSeconds(5)), server)
    constructor(port: Int, vararg inConnectors: ConnectorBuilder) : this(port, ServerConfig.StopMode.Graceful(Duration.ofSeconds(5)), *inConnectors)
    constructor(port: Int, stopMode: ServerConfig.StopMode, vararg inConnectors: ConnectorBuilder) : this(
        port,
        stopMode,
        Server(LoomThreadPool()).apply {
            inConnectors.forEach { addConnector(it(this)) }
        })

    init {
        when(stopMode) {
            is ServerConfig.StopMode.Graceful -> {
                server.apply {
                    stopTimeout = stopMode.timeout.toMillis()
                }
            }
            is ServerConfig.StopMode.Immediate -> throw ServerConfig.UnsupportedStopMode(stopMode)
        }
    }

    override fun toServer(http: HttpHandler?, ws: WsHandler?, sse: SseHandler?): Http4kServer {
        if (sse != null) throw UnsupportedOperationException("Jetty does not support sse")
        http?.let { server.insertHandler(http.toJettyHandler(stopMode is ServerConfig.StopMode.Graceful)) }
        ws?.let {
            server.insertHandler(
                WebSocketUpgradeHandler(
                    WebSocketComponents()
                ).apply {
                    addMapping("/*", it.toJettyNegotiator())
                })
        }

        return object : Http4kServer {
            override fun start(): Http4kServer = apply {
                server.start()
            }

            override fun stop(): Http4kServer = apply { server.stop() }

            override fun port(): Int = if (port > 0) port else server.uri.port
        }
    }
}

fun WsHandler.toJettyNegotiator() = object : WebSocketNegotiator.AbstractNegotiator() {
    override fun negotiate(negotiation: WebSocketNegotiation): FrameHandler {
        val request = negotiation.request.asHttp4kRequest()
        return Http4kWebSocketFrameHandler(this@toJettyNegotiator(request), request)
    }
}

fun HttpHandler.toJettyHandler(withStatisticsHandler: Boolean = false): HandlerWrapper = ServletContextHandler(org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS).apply {
    addServlet(ServletHolder(this@toJettyHandler.asServlet()), "/*")
}.let {
    if (withStatisticsHandler) StatisticsHandler().apply { handler = it } else it
}

typealias ConnectorBuilder = (Server) -> ServerConnector

fun http(httpPort: Int): ConnectorBuilder = { server: Server -> ServerConnector(server).apply { port = httpPort } }


class LoomThreadPool : ThreadPool {
    var executorService: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

    @Throws(InterruptedException::class)
    override fun join() {
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }

    override fun getThreads(): Int = 1

    override fun getIdleThreads(): Int = 1

    override fun isLowOnThreads(): Boolean = false

    override fun execute(command: Runnable) {
        executorService.submit(command)
    }
}