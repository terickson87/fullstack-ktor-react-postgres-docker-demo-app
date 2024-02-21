package io.github.terickson87.adapter

import io.github.terickson87.domain.NoteRequest
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeWithin
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

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
        val createNow = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        val createDuration = Duration.ofMillis(100)
        val noteRequest = NoteRequest(TEST_NOTE_BODY)
        val newDbNote = sqlNotesAccessor.createNote(noteRequest)
        newDbNote.id.value.shouldBeTypeOf<Int>()
        newDbNote.createdAt.shouldBeWithin(createDuration, createNow)
        newDbNote.updatedAt.shouldBeWithin(createDuration, createNow)
        newDbNote.body.shouldBe(TEST_NOTE_BODY)

        //Read
        val readDbNote = sqlNotesAccessor.getNoteById(newDbNote.id.value)
        readDbNote.shouldNotBeNull().id.value.shouldBe(newDbNote.id.value)
        readDbNote.createdAt.shouldBeWithin(createDuration, createNow)
        readDbNote.updatedAt.shouldBeWithin(createDuration, createNow)

        //Read All
        val readDbNotes = sqlNotesAccessor.getAllNotes()
        readDbNotes.size.shouldBe(1)
        readDbNotes[0].id.value.shouldBe(newDbNote.id.value)

        //Update
        val updateRequest = NoteRequest(UPDATED_NOTE_BODY)
        val updatedDbNote = sqlNotesAccessor.updateNoteById(newDbNote.id.value, updateRequest)
        updatedDbNote.shouldNotBeNull().id.value.shouldBe(newDbNote.id.value)
        updatedDbNote.createdAt.shouldBeWithin(createDuration, createNow)
        updatedDbNote.updatedAt.shouldBeAfter(updatedDbNote.createdAt)
        updatedDbNote.body.shouldBe(UPDATED_NOTE_BODY)

        //Delete
        val wasDeleted = sqlNotesAccessor.deleteNoteById(newDbNote.id.value)
        wasDeleted.shouldBeTrue()
        val deletedDbNote = sqlNotesAccessor.getNoteById(newDbNote.id.value)
        deletedDbNote.shouldBeNull()
    }

}