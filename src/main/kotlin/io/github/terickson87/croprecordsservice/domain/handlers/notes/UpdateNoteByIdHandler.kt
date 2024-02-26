package io.github.terickson87.croprecordsservice.domain.handlers.notes

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.domain.NoteResponse
import io.github.terickson87.croprecordsservice.domain.toNoteResponse

class UpdateNoteByIdHandler(private val notesAccessor: NotesAccessor) {
    data class Input(val id: Int, val body: String)

    sealed class Output() {
        data class Success(val noteResponse: NoteResponse) : Output()
        data class IdNotFound(val id: Int) : Output()
        data object EmptyBody : Output()
    }

    fun handle(input: Input): Output =
    if (input.body.isNotEmpty()) handleNonEmptyBody(input.id, input.body) else Output.EmptyBody

    private fun handleNonEmptyBody(id: Int, body: String): Output =
        notesAccessor.updateNoteById(id, body)
            ?.toNoteResponse()
            ?.let { Output.Success(it) }
            ?: Output.IdNotFound(id)
}