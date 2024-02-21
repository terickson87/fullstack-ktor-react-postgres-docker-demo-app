package io.github.terickson87.adapter

import io.github.terickson87.adapter.accessor.NotesAccessor
import io.github.terickson87.domain.Note
import io.github.terickson87.domain.NoteRequest
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


class SqlNotesAccessor(private val database: Database) : NotesAccessor {

    companion object {
        val DB_ZONE_OFFSET_UTC: ZoneOffset = ZoneOffset.UTC
    }

    object NotesTable : IntIdTable() {
        val createdAt = datetime("created_at")
        val updatedAt = datetime("updated_at")
        val body = text("body")
    }

    class DbNote(id: EntityID<Int>): IntEntity(id){
        companion object: IntEntityClass<DbNote>(NotesTable)
        var createdAt by NotesTable.createdAt
        var updatedAt by NotesTable.updatedAt
        var body by NotesTable.body
    }

    private fun DbNote.toNote(): Note =
        Note(this.id.value,
            this.createdAt.toInstant(DB_ZONE_OFFSET_UTC),
            this.updatedAt.toInstant(DB_ZONE_OFFSET_UTC),
            this.body)

    override fun createNote(noteRequest: NoteRequest): Note =
        transaction(database) {
            DbNote.new {
                body = noteRequest.body
                createdAt = LocalDateTime.ofInstant(Instant.now(), DB_ZONE_OFFSET_UTC);
                updatedAt = LocalDateTime.ofInstant(Instant.now(), DB_ZONE_OFFSET_UTC);
            }
        }.toNote()

    override fun getAllNotes(): List<Note> =
        transaction(database) {
            DbNote.all().toList()
                .map { it.toNote() }
        }

    override fun getNoteById(id: Int): Note? =
        getDbNoteById(id)?.toNote()

    private fun getDbNoteById(id: Int): DbNote? =
        transaction(database) {
            DbNote.findById(id)
        }

    override fun updateNoteById(id: Int, noteRequest: NoteRequest): Note? =
        getDbNoteById(id)?.let {
            transaction(database) {
                it.body = noteRequest.body
                it.updatedAt = LocalDateTime.ofInstant(Instant.now(), DB_ZONE_OFFSET_UTC);
            }
        }?.let {
            getNoteById(id)
        }

    override fun deleteNoteById(id: Int): Boolean =
        transaction(database) {
            DbNote.findById(id)?.delete()?.let { true } ?: false
        }
}