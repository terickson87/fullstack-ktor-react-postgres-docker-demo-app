package io.github.terickson87.adapter.accessor

import io.github.terickson87.adapter.SqlNotesAccessor
import io.github.terickson87.domain.Note
import io.github.terickson87.domain.NoteRequest

interface NotesAccessor {
    fun createNote(noteRequest: NoteRequest): Note
    fun getAllNotes(): List<Note>
    fun getNoteById(id: Int): Note?
    fun updateNoteById(id: Int, noteRequest: NoteRequest): Note?
    fun deleteNoteById(id: Int): Boolean
}