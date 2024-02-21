package io.github.terickson87.adapter

import io.github.terickson87.adapter.accessor.NotesAccessor
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

    override fun createNote(noteRequest: NoteRequest): DbNote =
        transaction(database) {
            DbNote.new {
                body = noteRequest.body
                createdAt = LocalDateTime.ofInstant(Instant.now(), DB_ZONE_OFFSET_UTC);
                updatedAt = LocalDateTime.ofInstant(Instant.now(), DB_ZONE_OFFSET_UTC);
            }
        }

    override fun getAllNotes(): List<DbNote> =
        transaction(database) {
            DbNote.all().toList()
        }

    override fun getNoteById(id: Int): DbNote? =
        transaction(database) {
            DbNote.findById(id)
        }

    override fun updateNoteById(id: Int, noteRequest: NoteRequest): DbNote? =
        getNoteById(id)?.let {
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