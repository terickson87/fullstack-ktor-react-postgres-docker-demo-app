package io.github.terickson87.ktorpostgresdemo.util

import io.github.terickson87.ktorpostgresdemo.adapter.accessor.NotesAccessor
import io.github.terickson87.ktorpostgresdemo.plugins.configureJson
import io.github.terickson87.ktorpostgresdemo.plugins.configureRouting
import io.github.terickson87.ktorpostgresdemo.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.mockk
import kotlinx.serialization.json.Json

class RouteTestFuncs {
    companion object {}
}

fun ApplicationTestBuilder.buildClient(): HttpClient =
    createClient {
        this.install(ContentNegotiation) {
            json(Json {
                configureJson()
            })
        }
    }

fun RouteTestFuncs.Companion.baseCall(notesAccessor: NotesAccessor? = null, block: suspend (HttpClient) -> Unit): Unit =
    testApplication {
        application {
            configureSerialization()
            configureRouting(notesAccessor ?: mockk())
        }
        val client = buildClient()
        block(client)
    }

fun RouteTestFuncs.Companion.testGet(
    path: String,
    notesAccessor: NotesAccessor,
    verifyBlock: (HttpResponse) -> Unit): Unit =
    baseCall(notesAccessor) {
        it.get(path)
            .apply { verifyBlock(this) }
    }

inline fun <reified T> RouteTestFuncs.Companion.testPostJsonBody(
    path: String,
    bodyToSet: T,
    notesAccessor: NotesAccessor,
    crossinline verifyBlock: (HttpResponse) -> Unit): Unit =
    baseCall(notesAccessor) {
        it.post(path) {
            contentType(ContentType.Application.Json)
            setBody(bodyToSet)
        }
            .apply { verifyBlock(this) }
    }