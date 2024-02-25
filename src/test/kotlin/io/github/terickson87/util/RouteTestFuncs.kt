package io.github.terickson87.util

import io.github.terickson87.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.mockk

class RouteTestFuncs {
    companion object {}
}

fun RouteTestFuncs.Companion.testGet(path: String, verifyBlock: (HttpResponse) -> Unit) =
    testApplication {
        application {
            configureRouting()
        }
        client.get(path).apply { verifyBlock(this) }
    }

inline fun <reified T> RouteTestFuncs.Companion.testPostJsonBody(
    path: String,
    bodyToSet: T,
    crossinline verifyBlock: (HttpResponse) -> Unit) =
    testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        application {
            configureRouting()
        }

        client.post(path) {
            contentType(ContentType.Application.Json)
            setBody(bodyToSet)
        }
            .apply { verifyBlock(this) }
    }