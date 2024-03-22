package io.github.terickson87.ktorpostgresdemo.domain.handlers.notes

import io.github.terickson87.ktorpostgresdemo.adapter.accessor.NotesAccessor
import io.github.terickson87.ktorpostgresdemo.domain.NoteResponse
import io.github.terickson87.ktorpostgresdemo.domain.toNoteResponse

class GetNoteByIdHandler(private val notesAccessor: NotesAccessor) {
    data class Input(val id: Int)
    sealed class Output {
        data class Success(val noteResponse: NoteResponse) : Output()
        data class IdNotFound(val id: Int) : Output()
    }

    fun handle(input: Input): Output =
        notesAccessor.getNoteById(input.id)
            ?.toNoteResponse()
            ?.let { Output.Success(it) }
            ?: Output.IdNotFound(input.id)
}