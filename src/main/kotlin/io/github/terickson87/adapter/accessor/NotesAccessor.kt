package io.github.terickson87.adapter.accessor

import io.github.terickson87.domain.Note

interface NotesAccessor {
    fun createNote(createBody: String): Note
    fun getAllNotes(): List<Note>
    fun getNoteById(id: Int): Note?
    fun updateNoteById(id: Int, body: String): Note?
    fun deleteNoteById(id: Int): Boolean
}