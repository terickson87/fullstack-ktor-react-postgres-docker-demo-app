package io.github.terickson87.adapter.accessor

import io.github.terickson87.adapter.SqlNotesAccessor
import io.github.terickson87.domain.NoteRequest

interface NotesAccessor {
    fun createNote(noteRequest: NoteRequest): SqlNotesAccessor.DbNote
    fun getAllNotes(): List<SqlNotesAccessor.DbNote>
    fun getNoteById(id: Int): SqlNotesAccessor.DbNote?
    fun updateNoteById(id: Int, noteRequest: NoteRequest): SqlNotesAccessor.DbNote?
    fun deleteNoteById(id: Int): Boolean
}