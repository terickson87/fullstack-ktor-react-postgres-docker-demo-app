package io.github.terickson87.croprecordsservice.domain.handlers.notes

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.domain.NoteResponse
import io.github.terickson87.croprecordsservice.domain.toNoteResponse

class CreateNoteHandler(private val notesAccessor: NotesAccessor) {
    data class Input(val body: String)

    sealed class Output() {
        data class Success(val noteResponse: NoteResponse) : Output()
        data object EmptyBody : Output()
    }

    fun handle(input: Input): Output =
        if (input.body.isNotEmpty()) handleNonEmptyBody(input.body) else Output.EmptyBody

    private fun handleNonEmptyBody(body: String): Output =
        notesAccessor.createNote(body)
            .toNoteResponse()
            .let { Output.Success(it) }
}