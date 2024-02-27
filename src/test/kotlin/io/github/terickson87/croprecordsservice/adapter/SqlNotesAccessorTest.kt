package io.github.terickson87.croprecordsservice.adapter

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.date.shouldBeCloseTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SqlNotesAccessorTest : AnnotationSpec() {

    // TODO Change to Kotest FunSpec and use prepareSpec and finalizeSpec for BeforeAll and AfterAll respectively

    // TODO look into using https://kotest.io/docs/extensions/test_containers.html

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
        val newNote = sqlNotesAccessor.createNote(TEST_NOTE_BODY + "1")
        newNote.id.shouldBeTypeOf<Int>()
        newNote.body.shouldBe(TEST_NOTE_BODY + "1")
        val afterCreate = Instant.now()
        newNote.createdAt.shouldBeAfter(beforeCreate)
        newNote.createdAt.shouldBeBefore(afterCreate)
        newNote.updatedAt.shouldBeAfter(beforeCreate)
        newNote.updatedAt.shouldBeBefore(afterCreate)
        val createdNotes = listOf(newNote) + (2..10)
            .map { sqlNotesAccessor.createNote(TEST_NOTE_BODY + "$it") }

        //Read
        val readNote = sqlNotesAccessor.getNoteById(newNote.id)
        readNote.shouldNotBeNull().id.shouldBe(newNote.id)
        readNote.createdAt.shouldBeAfter(beforeCreate)
        readNote.createdAt.shouldBeBefore(afterCreate)
        readNote.updatedAt.shouldBeAfter(beforeCreate)
        readNote.updatedAt.shouldBeBefore(afterCreate)

        //Read All
        val readNotes = sqlNotesAccessor.getAllNotes()
        readNotes.values.size.shouldBe(10)
        readNotes.continuation.shouldBeNull()
        readNotes.values[0].id.shouldBe(newNote.id)

        //Read All paged
        val page1 = sqlNotesAccessor.getAllNotes(1,0)
        page1.continuation.shouldNotBeNull().shouldBe(1)
        val page2 = sqlNotesAccessor.getAllNotes(1, 1)
        page2.continuation.shouldBe(2)
        val page3 = sqlNotesAccessor.getAllNotes(2, 2)
        page3.continuation.shouldNotBeNull().shouldBe(4)
        val page4 = sqlNotesAccessor.getAllNotes(6, 4)
        val allPageValues = page1.values + page2.values + page3.values + page4.values
        allPageValues.size.shouldBe(10)
        allPageValues.forEachIndexed { i, iNote ->
            val iCreatedNote = createdNotes[i]
            iNote.id.shouldBe(iCreatedNote.id)
            iNote.body.shouldBe(iCreatedNote.body)
            /* There is a diff here in the times, its may have to do with a rounding error between the DSL vs DAO access
             E.g. createdAt=2024-02-27T15:48:29.070901254Z vs
                  createdAt=2024-02-27T15:48:29.070901Z
                                        where 0.000001 seconds is 1 microsecond is 1000 nanoseconds
                  E.g. the diff of .070901254 - .070901 is 0.254 microseconds
            The difference between any two notes created in the second batch is on the order of 0.2 seconds,
            E.g. .138615791 and .156233031. 0.2 seconds is 200000 microseconds.*/
            iNote.createdAt.shouldBeCloseTo(iCreatedNote.createdAt, 1.toDuration(DurationUnit.MICROSECONDS))
            iNote.createdAt.shouldBeCloseTo(iCreatedNote.createdAt, 1.toDuration(DurationUnit.MICROSECONDS))
        }

        //Update
        val beforeUpdate = Instant.now()
        val updatedDbNote = sqlNotesAccessor.updateNoteById(newNote.id, UPDATED_NOTE_BODY)
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