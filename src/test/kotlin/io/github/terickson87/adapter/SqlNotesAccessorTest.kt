package io.github.terickson87.adapter

import io.github.terickson87.domain.NoteRequest
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Instant

class SqlNotesAccessorTest : AnnotationSpec() {

    companion object {
        private const val DB_NAME = "test-db"
        private const val DB_USER = "test-user"
        private const val DB_PASSWORD = "test-password"
        val testPostgresContainer: PostgreSQLContainer<Nothing> =
            PostgreSQLContainer<Nothing>("postgres:16").apply {
                withDatabaseName(DB_NAME)
                withUsername(DB_USER)
                withPassword(DB_PASSWORD)
            }
        const val TEST_NOTE_BODY = "Test Note Body"
        const val UPDATED_NOTE_BODY = "Updated Test Note Body"

        lateinit var sqlNotesAccessor: SqlNotesAccessor
    }

    @BeforeAll
    fun setup() {
        testPostgresContainer.start()

        val database = Database.connect(
            url = testPostgresContainer.jdbcUrl,
            user = testPostgresContainer.username,
            password = testPostgresContainer.password
        )

        transaction(database) {
            SchemaUtils.create(SqlNotesAccessor.NotesTable)
        }

        sqlNotesAccessor = SqlNotesAccessor(database)
    }

    @AfterAll
    fun teardown() {
        testPostgresContainer.stop()
    }

    @Test
    fun crudNote() = runBlocking {
        //Create
        val beforeCreate = Instant.now()
        val noteRequest = NoteRequest(TEST_NOTE_BODY)
        val newNote = sqlNotesAccessor.createNote(noteRequest)
        newNote.id.shouldBeTypeOf<Int>()
        newNote.body.shouldBe(TEST_NOTE_BODY)
        val afterCreate = Instant.now()
        newNote.createdAt.shouldBeAfter(beforeCreate)
        newNote.createdAt.shouldBeBefore(afterCreate)
        newNote.updatedAt.shouldBeAfter(beforeCreate)
        newNote.updatedAt.shouldBeBefore(afterCreate)

        //Read
        val readNote = sqlNotesAccessor.getNoteById(newNote.id)
        readNote.shouldNotBeNull().id.shouldBe(newNote.id)
        readNote.createdAt.shouldBeAfter(beforeCreate)
        readNote.createdAt.shouldBeBefore(afterCreate)
        readNote.updatedAt.shouldBeAfter(beforeCreate)
        readNote.updatedAt.shouldBeBefore(afterCreate)

        //Read All
        val readNotes = sqlNotesAccessor.getAllNotes()
        readNotes.size.shouldBe(1)
        readNotes[0].id.shouldBe(newNote.id)

        //Update
        val beforeUpdate = Instant.now()
        val updateRequest = NoteRequest(UPDATED_NOTE_BODY)
        val updatedDbNote = sqlNotesAccessor.updateNoteById(newNote.id, updateRequest)
        updatedDbNote.shouldNotBeNull().id.shouldBe(newNote.id)
        updatedDbNote.body.shouldBe(UPDATED_NOTE_BODY)
        val afterUpdate = Instant.now()
        updatedDbNote.createdAt.shouldBeAfter(beforeCreate)
        updatedDbNote.createdAt.shouldBeBefore(afterCreate)
        updatedDbNote.updatedAt.shouldBeAfter(beforeUpdate)
        updatedDbNote.updatedAt.shouldBeBefore(afterUpdate)

        //Delete
        val wasDeleted = sqlNotesAccessor.deleteNoteById(newNote.id)
        wasDeleted.shouldBeTrue()
        val deletedDbNote = sqlNotesAccessor.getNoteById(newNote.id)
        deletedDbNote.shouldBeNull()
    }

}