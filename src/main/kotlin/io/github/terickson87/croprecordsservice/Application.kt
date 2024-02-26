package io.github.terickson87.croprecordsservice

import io.github.terickson87.croprecordsservice.adapter.PostgresSingleton
import io.github.terickson87.croprecordsservice.adapter.SqlNotesAccessor
import io.github.terickson87.croprecordsservice.plugins.configureHTTP
import io.github.terickson87.croprecordsservice.plugins.configureMonitoring
import io.github.terickson87.croprecordsservice.plugins.configureRouting
import io.github.terickson87.croprecordsservice.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    PostgresSingleton.init()
    configureSerialization()
    configureHTTP()
    configureMonitoring()
    configureRouting(SqlNotesAccessor(PostgresSingleton.getDatabase()))
}
