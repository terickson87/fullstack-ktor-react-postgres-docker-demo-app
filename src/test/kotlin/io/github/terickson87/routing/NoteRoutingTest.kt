package io.github.terickson87.routing

import io.github.terickson87.adapter.accessor.NotesAccessor
import io.github.terickson87.domain.Note
import io.github.terickson87.domain.NoteRequest
import io.github.terickson87.util.RouteTestFuncs
import io.github.terickson87.util.testGet
import io.github.terickson87.util.testPostJsonBody
import io.github.terickson87.domain.toNoteResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.assertEquals

class NoteRoutingTest : FunSpec({
    val noteId = 17
    val createdAt = LocalDateTime.parse("2024-02-25T10:15:00").atZone(ZoneId.of("America/Phoenix")).toInstant()
    val updatedAt = LocalDateTime.parse("2024-02-25T10:26:30").atZone(ZoneId.of("America/Phoenix")).toInstant()
    val note = Note(17, createdAt, updatedAt, "Test Body")
    val noteResponse = note.toNoteResponse()

    val notesAccessorMock: NotesAccessor = mockk()
    every { notesAccessorMock.getNoteById(any())} returns note

    test("/notes/all should return as expected") {
        RouteTestFuncs.testGet("/notes/all") {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals("Get All Notes", it.bodyAsText())
            }
        }
    }

    test("/notes/{id} should return as expected") {
        RouteTestFuncs.testGet("/notes/$noteId", notesAccessorMock) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals(noteResponse, it.body())
            }
        }
    }

    test("/notes/new should return as expected") {
        val newBody = """{ "body": "Test New Body" }"""
        val noteRequest = NoteRequest(newBody)
        val noteRequestJson = Json.encodeToString(noteRequest)
        val newNote = note.copy(body = newBody)
        val newNoteResponse = newNote.toNoteResponse()
        every { notesAccessorMock.createNote(any()) } returns newNote
        RouteTestFuncs.testPostJsonBody("/notes/new", noteRequestJson, notesAccessorMock) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals(newNoteResponse, it.body())
            }
        }
    }

    test("/notes/update should return as expected") {
        val updateBody = """{ "field": "update" }"""
        RouteTestFuncs.testPostJsonBody("/notes/update", updateBody) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals("Post update contents:'$updateBody'", it.bodyAsText())
            }
        }
    }

    test("/notes/delete should return as expected") {
        val deleteBody = """{ "field": "delete" }"""
        RouteTestFuncs.testPostJsonBody("/notes/delete", deleteBody) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals("Post delete contents:'$deleteBody'", it.bodyAsText())
            }
        }
    }
})