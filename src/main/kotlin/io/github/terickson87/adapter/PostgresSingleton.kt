package io.github.terickson87.adapter

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object PostgresSingleton {

    private lateinit var database: Database
    fun init() {
        database = Database.connect(
            url = "jdbc:postgresql://postgres:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )

        transaction(database) {
            SchemaUtils.create(SqlNotesAccessor.NotesTable)
        }
    }

    fun getDatabase(): Database =
        when(this::database.isInitialized) {
            true -> database
            false -> {
                init()
                database
            }
        }
}