package io.github.terickson87.ktorpostgresdemo.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.*
//import com.codahale.metrics.*
//import io.ktor.server.metrics.dropwizard.*
//import java.util.concurrent.TimeUnit

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc("call-id")
    }
    // install(DropwizardMetrics) {
    //     Slf4jReporter.forRegistry(registry)
    //         .outputTo(this@configureMonitoring.log)
    //         .convertRatesTo(TimeUnit.SECONDS)
    //         .convertDurationsTo(TimeUnit.MILLISECONDS)
    //         .build()
    //         .start(10, TimeUnit.SECONDS)
    // }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
}
