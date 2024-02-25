package io.github.terickson87.routing

import io.github.terickson87.util.RouteTestFuncs
import io.github.terickson87.util.testGet
import io.github.terickson87.util.testPostJsonBody
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

class NoteRoutingTest : FunSpec({
    test("A call to /notes/all should return as expected") {
        RouteTestFuncs.testGet("/notes/all") {
            runBlocking {
                assertEquals(HttpStatusCode.OK, it.status)
                assertEquals("Get All Notes", it.bodyAsText())
            }
        }
    }

    test("A call to /notes/{id} should return as expected") {
        val id = 17
        RouteTestFuncs.testGet("/notes/$id") {
            runBlocking {
                assertEquals(HttpStatusCode.OK, it.status)
                assertEquals("Get id #$id", it.bodyAsText())
            }
        }
    }

    test("A call to /notes/new should return as expected") {
        val newBody = """{ "field": "new" }"""
        RouteTestFuncs.testPostJsonBody("/notes/new", newBody) {
            runBlocking {
                assertEquals(HttpStatusCode.OK, it.status)
                assertEquals("Post new contents:'$newBody'", it.bodyAsText())
            }
        }
    }

    test("A call to /notes/update should return as expected") {
        val updateBody = """{ "field": "update" }"""
        RouteTestFuncs.testPostJsonBody("/notes/update", updateBody) {
            runBlocking {
                assertEquals(HttpStatusCode.OK, it.status)
                assertEquals("Post update contents:'$updateBody'", it.bodyAsText())
            }
        }
    }

    test("A call to /notes/delete should return as expected") {
        val deleteBody = """{ "field": "delete" }"""
        RouteTestFuncs.testPostJsonBody("/notes/delete", deleteBody) {
            runBlocking {
                assertEquals(HttpStatusCode.OK, it.status)
                assertEquals("Post delete contents:'$deleteBody'", it.bodyAsText())
            }
        }
    }
})