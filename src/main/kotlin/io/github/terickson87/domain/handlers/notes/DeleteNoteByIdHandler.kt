package io.github.terickson87.domain.handlers.notes

import io.github.terickson87.adapter.accessor.NotesAccessor

class DeleteNoteByIdHandler(private val notesAccessor: NotesAccessor) {
    data class Input(val id: Int)
    sealed class Output {
        data object Success : Output()
        data class IdNotFound(val id: Int) : Output()
    }

    fun handle(input: Input): Output =
        notesAccessor.deleteNoteById(input.id)
            .let {
                when (it) {
                    true -> Output.Success
                    false -> Output.IdNotFound(input.id)
                }
            }
}