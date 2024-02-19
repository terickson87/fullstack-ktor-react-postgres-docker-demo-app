package io.github.terickson87.adapter

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
    fun init() {
        val database = Database.connect(
            url = "jdbc:postgresql://localhost:5438/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )

        transaction(database) {
            SchemaUtils.create(NotesService.NotesTable)
        }
    }
}