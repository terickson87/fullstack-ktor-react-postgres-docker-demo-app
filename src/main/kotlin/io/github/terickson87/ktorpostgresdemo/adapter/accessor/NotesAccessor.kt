package io.github.terickson87.ktorpostgresdemo.adapter.accessor

interface NotesAccessor {
    fun createNote(createBody: String): Note
    fun getAllNotes(n: Int? = null, offset: Long? = null): PagedData<Note, Long>
    fun getNoteById(id: Int): Note?
    fun updateNoteById(id: Int, body: String): Note?
    fun deleteNoteById(id: Int): Boolean
}