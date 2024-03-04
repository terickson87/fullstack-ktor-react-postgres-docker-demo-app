package io.github.terickson87.croprecordsservice.adapter

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object PostgresSingleton {

    private lateinit var database: Database

    // TODO Add in HikariCP https://github.com/brettwooldridge/HikariCP
    // TODO add Flyway https://proandroiddev.com/how-to-deploy-a-ktor-server-using-docker-postgresql-and-flyway-a-journey-to-effortless-24464b0752de
    //  & https://github.com/limadelrey/vertx-4-reactive-rest-api/blob/master/src/main/resources/db/migration/V1__initial_schema.sql
    // TODO add adminer

    fun init() {
        database = Database.connect(
            url = "jdbc:postgresql://postgres:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres",

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