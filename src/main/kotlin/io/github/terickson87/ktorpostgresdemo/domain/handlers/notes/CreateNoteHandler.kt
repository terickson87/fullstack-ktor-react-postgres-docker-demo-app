package io.github.terickson87.ktorpostgresdemo.domain.handlers.notes

import io.github.terickson87.ktorpostgresdemo.adapter.accessor.NotesAccessor
import io.github.terickson87.ktorpostgresdemo.domain.NoteResponse
import io.github.terickson87.ktorpostgresdemo.domain.toNoteResponse

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