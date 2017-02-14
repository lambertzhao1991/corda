package net.corda.demobench.model

import net.corda.demobench.loggerFor
import org.h2.server.web.LocalWebServer
import org.h2.tools.Server
import org.h2.util.JdbcUtils
import java.util.concurrent.Executors
import kotlin.reflect.jvm.jvmName

class DBViewer : AutoCloseable {
    private companion object {
        val log = loggerFor<DBViewer>()
    }

    private val webServer: Server
    private val pool = Executors.newCachedThreadPool()

    init {
        val ws = LocalWebServer()
        webServer = Server(ws, "-webPort", "0")
        ws.setShutdownHandler(webServer)

        webServer.setShutdownHandler {
            webServer.stop()
        }

        pool.submit {
            webServer.start()
        }
    }

    override fun close() {
        log.info("Shutting down")
        pool.shutdown()
        webServer.shutdown()
    }

    fun openBrowser(h2Port: Int) {
        val conn = JdbcUtils.getConnection(
            org.h2.Driver::class.jvmName,
            "jdbc:h2:tcp://localhost:$h2Port/node",
            "sa",
            ""
        )

        val url = (webServer.service as LocalWebServer).addSession(conn)
        log.info("Session: {}", url)

        pool.execute {
            Server.openBrowser(url)
        }
    }

}
