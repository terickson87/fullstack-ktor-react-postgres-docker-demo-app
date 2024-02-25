package io.github.terickson87.domain.handlers.notes

import io.github.terickson87.adapter.accessor.NotesAccessor
import io.github.terickson87.domain.Note
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

class CreateNoteHandlerTest : BehaviorSpec ({

    val notesAccessorMock: NotesAccessor = mockk()
    val createNoteHandler = CreateNoteHandler(notesAccessorMock)

    val noteNow = Instant.now()
    val testNote = Note(17, noteNow, noteNow, "test Note Body")

    given("Create note with valid body") {
        val input = CreateNoteHandler.Input(testNote.body)
        every { notesAccessorMock.createNote(testNote.body) } returns testNote

        `when`("handle") {
            val result = createNoteHandler.handle(input)

            then("return Success") {
                result.shouldBeTypeOf<CreateNoteHandler.Output.Success>().noteResponse.also {
                    it.id.shouldBe(testNote.id)
                    it.createdAt.shouldBe(testNote.createdAt)
                    it.modifiedAt.shouldBe(testNote.updatedAt)
                    it.body.shouldBe(testNote.body)
                }
            }
        }
    }

    given("Create note with empty body") {
        val input = CreateNoteHandler.Input("")

        `when`("handle") {
            val result = createNoteHandler.handle(input)

            then("Returns EmptyBody") {
                result.shouldBeTypeOf<CreateNoteHandler.Output.EmptyBody>()
            }
        }
    }
})