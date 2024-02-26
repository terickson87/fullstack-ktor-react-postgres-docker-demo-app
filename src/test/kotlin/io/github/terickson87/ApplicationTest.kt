package io.github.terickson87

import io.github.terickson87.util.RouteTestFuncs
import io.github.terickson87.util.testGet
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class ApplicationTest : FunSpec({

    test("A call to '/' should return as expected") {
        RouteTestFuncs.testGet("/", mockk()) {
            runBlocking {
                assertEquals(HttpStatusCode.OK, it.status)
                assertEquals("Hello World!", it.bodyAsText())
            }
        }
    }
})
