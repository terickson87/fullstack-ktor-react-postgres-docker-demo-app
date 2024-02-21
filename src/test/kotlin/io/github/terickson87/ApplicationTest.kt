package io.github.terickson87

import io.github.terickson87.plugins.*
import io.kotest.core.spec.style.AnnotationSpec
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest : AnnotationSpec(){
    @Test
    fun testRootHello(): Unit = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testGetAllNotes(): Unit = testApplication {
        application {
            configureRouting()
        }
        client.get("/notes/all").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Get All Skus", bodyAsText())
        }
    }

    @Test
    fun testGetNoteById(): Unit = testApplication {
        application {
            configureRouting()
        }
        val id = 17
        client.get("/notes/$id").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Get id #$id", bodyAsText())
        }
    }

    @Test
    fun testNewNote(): Unit = testApplication {
        application {
            configureRouting()
        }
        val newBody = """{ "field": "new" }"""
        client.post("/notes/new") {
            setBody(newBody)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Post new contents:'$newBody'", bodyAsText())
        }
    }

    @Test
    fun testUpdateNote(): Unit = testApplication {
        application {
            configureRouting()
        }
        val updateBody = """{ "field": "update" }"""
        client.post("/notes/update") {
            setBody(updateBody)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Post update contents:'$updateBody'", bodyAsText())
        }
    }

    @Test
    fun testDeleteNote(): Unit = testApplication {
        application {
            configureRouting()
        }
        val deleteBody = """{ "field": "delete" }"""
        client.post("/notes/delete") {
            setBody(deleteBody)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Post delete contents:'$deleteBody'", bodyAsText())
        }
    }
}
