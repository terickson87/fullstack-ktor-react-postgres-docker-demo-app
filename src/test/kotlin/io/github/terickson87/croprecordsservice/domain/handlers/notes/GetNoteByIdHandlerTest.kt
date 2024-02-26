package io.github.terickson87.croprecordsservice.domain.handlers.notes

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.domain.Note
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

class GetNoteByIdHandlerTest : BehaviorSpec ({

    val notesAccessorMock: NotesAccessor = mockk()
    val getNotesByIdHandler = GetNoteByIdHandler(notesAccessorMock)

    val noteNow = Instant.now()
    val testNote = Note(17, noteNow, noteNow, "test Note Body")

    given("Integer id that is in the database") {
        val input = GetNoteByIdHandler.Input(testNote.id)
        every { notesAccessorMock.getNoteById(testNote.id) } returns testNote

        `when`("handle") {
            val result = getNotesByIdHandler.handle(input)

            then("returns Output.MissingID")
            result.shouldBeTypeOf<GetNoteByIdHandler.Output.Success>().noteResponse.also {
                it.id.shouldBe(testNote.id)
                it.createdAt.shouldBe(noteNow)
                it.modifiedAt.shouldBe(noteNow)
                it.body.shouldBe(testNote.body)
            }
        }
    }

    given("Integer id that is NOT in the database") {
        val input = GetNoteByIdHandler.Input(testNote.id)
        every { notesAccessorMock.getNoteById(testNote.id) } returns null

        `when`("handle") {
            val result = getNotesByIdHandler.handle(input)

            then("returns Output.MissingID")
            result.shouldBeTypeOf<GetNoteByIdHandler.Output.IdNotFound>().also {
                it.id.shouldBe(testNote.id)
            }
        }
    }
})