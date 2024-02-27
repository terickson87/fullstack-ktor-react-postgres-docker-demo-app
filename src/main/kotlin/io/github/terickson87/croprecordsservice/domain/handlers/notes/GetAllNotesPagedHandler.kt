package io.github.terickson87.croprecordsservice.domain.handlers.notes

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.domain.AllNotesResponsePage
import io.github.terickson87.croprecordsservice.domain.PageInfo
import io.github.terickson87.croprecordsservice.domain.toNoteResponse

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