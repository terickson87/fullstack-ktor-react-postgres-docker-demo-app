package io.github.terickson87.croprecordsservice.routing

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.adapter.accessor.PagedData
import io.github.terickson87.croprecordsservice.domain.AllNotesResponsePage
import io.github.terickson87.croprecordsservice.domain.Note
import io.github.terickson87.croprecordsservice.domain.NewNoteRequest
import io.github.terickson87.croprecordsservice.domain.PageInfo
import io.github.terickson87.croprecordsservice.util.RouteTestFuncs
import io.github.terickson87.croprecordsservice.util.testGet
import io.github.terickson87.croprecordsservice.util.testPostJsonBody
import io.github.terickson87.croprecordsservice.domain.toNoteResponse
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

    test("/notes/all should return as expected") {
        every { notesAccessorMock.getAllNotes(any(), any()) } returns PagedData(listOf(note), null)
        val allNotesResponsePage = AllNotesResponsePage(listOf(noteResponse), PageInfo(1, null))
        val pageInfo = PageInfo(1, 1L)
        val pageInfoJson = Json.encodeToString(pageInfo)
        RouteTestFuncs.testPostJsonBody("/notes/all", pageInfoJson, notesAccessorMock) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                it.body<AllNotesResponsePage>().shouldBe(allNotesResponsePage)
            }
        }
    }

    test("/notes/{id} should return as expected") {
        every { notesAccessorMock.getNoteById(noteId)} returns note
        RouteTestFuncs.testGet("/notes/$noteId", notesAccessorMock) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals(noteResponse, it.body())
            }
        }
    }

    test("/notes/new should return as expected") {
        val newBody = "Test New Body"
        val newNoteRequest = NewNoteRequest(newBody)
        val noteRequestJson = Json.encodeToString(newNoteRequest)
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
        val updateBody = "Test Updated Body"
        val newNoteRequest = NewNoteRequest(updateBody)
        val noteRequestJson = Json.encodeToString(newNoteRequest)
        val updateNote = note.copy(body = updateBody)
        val updateNoteResponse = updateNote.toNoteResponse()
        every { notesAccessorMock.updateNoteById(noteId, any()) } returns updateNote
        RouteTestFuncs.testPostJsonBody("/notes/update/$noteId", noteRequestJson, notesAccessorMock) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals(updateNoteResponse, it.body())
            }
        }
    }

    test("/notes/delete should return as expected") {
        every { notesAccessorMock.deleteNoteById(noteId) } returns true
        RouteTestFuncs.testGet("/notes/delete/$noteId", notesAccessorMock) {
            runBlocking {
                it.status.shouldBe(HttpStatusCode.OK)
                assertEquals("Note ID: '${noteId}' was deleted successfully.", it.bodyAsText())
            }
        }
    }
})