package io.github.terickson87.croprecordsservice.adapter

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.adapter.accessor.PagedData
import io.github.terickson87.croprecordsservice.domain.Note
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


class SqlNotesAccessor(private val database: Database) : NotesAccessor {

    companion object {
        val DB_ZONE_OFFSET_UTC: ZoneOffset = ZoneOffset.UTC
        const val DEFAULT_GET_ALL_PAGE_SIZE = 20;
        const val DEFAULT_GET_ALL_PAGE_OFFSET = 0L;
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

    private fun ResultRow.toNote(): Note =
        Note(this[NotesTable.id].value,
            this[NotesTable.createdAt].toInstant(DB_ZONE_OFFSET_UTC),
            this[NotesTable.updatedAt].toInstant(DB_ZONE_OFFSET_UTC),
            this[NotesTable.body]
        )

    override fun createNote(createBody: String): Note =
        transaction(database) {
            DbNote.new {
                val localDateTimeNowUtc = getLocalDateTimeNowUtc()
                body = createBody
                createdAt = localDateTimeNowUtc
                updatedAt = localDateTimeNowUtc
            }
        }.toNote()

    override fun getAllNotes(n: Int?, offset: Long?): PagedData<Note, Long> =
        (n ?: DEFAULT_GET_ALL_PAGE_SIZE).let { thisPageSize ->
            (offset ?: DEFAULT_GET_ALL_PAGE_OFFSET).let { thisOffset ->
                transaction(database) {
                     NotesTable.selectAll().limit(thisPageSize, thisOffset)
                        .map { it.toNote() }
                        .let{ PagedData(it, if (it.size < thisPageSize) null else thisOffset+thisPageSize)}
                }
            }
        }

    override fun getNoteById(id: Int): Note? =
        getDbNoteById(id)?.toNote()

    private fun getDbNoteById(id: Int): DbNote? =
        transaction(database) {
            DbNote.findById(id)
        }

    override fun updateNoteById(id: Int, body: String): Note? =
        transaction(database) {
            DbNote.findByIdAndUpdate(id) {
                it.body = body
                it.updatedAt = getLocalDateTimeNowUtc()
            }
        }?.toNote()

    override fun deleteNoteById(id: Int): Boolean =
        transaction(database) {
            DbNote.findById(id)?.delete()?.let { true } ?: false
        }

    private fun getLocalDateTimeNowUtc(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.now(), DB_ZONE_OFFSET_UTC)
}