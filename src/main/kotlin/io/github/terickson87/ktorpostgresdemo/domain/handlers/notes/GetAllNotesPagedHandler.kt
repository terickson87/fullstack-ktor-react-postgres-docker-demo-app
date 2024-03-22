package io.github.terickson87.ktorpostgresdemo.domain.handlers.notes

import io.github.terickson87.ktorpostgresdemo.adapter.accessor.NotesAccessor
import io.github.terickson87.ktorpostgresdemo.domain.AllNotesResponsePage
import io.github.terickson87.ktorpostgresdemo.domain.PageInfo
import io.github.terickson87.ktorpostgresdemo.domain.toNoteResponse

class GetAllNotesPagedHandler(private val notesAccessor: NotesAccessor) {
    data class Input(val pageSize: Int?, val offset: Long?)
    sealed class Output {
        data class Success(val allNotesResponsePage: AllNotesResponsePage) : Output()
    }

    fun handle(input: Input): Output =
        notesAccessor.getAllNotes(input.pageSize, input.offset)
            .let { pagedData ->
                pagedData.values.map { it.toNoteResponse() }
                    .let { AllNotesResponsePage(it, PageInfo(input.pageSize, pagedData.continuation)) }
                    .let { Output.Success(it) }
            }
}