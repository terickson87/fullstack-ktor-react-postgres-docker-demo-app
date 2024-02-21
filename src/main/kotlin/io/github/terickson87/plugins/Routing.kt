package io.github.terickson87.plugins

import io.github.terickson87.adapter.SqlNotesAccessor
import io.github.terickson87.adapter.PostgresSingleton
import io.github.terickson87.domain.NoteResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        noteRouting()
    }
}

// TODO move each route's logic to its own handler & manager
fun Route.noteRouting() {
    route("/notes") {
        get("/all") {
            call.respondText("Get All Skus")
        }
        get("/{id?}") {
            call.parameters["id"]
                ?.let { call.respondText("Get id #$it") }
                ?: call.respondText("Missing ID", status = HttpStatusCode.BadRequest)
        }
        post("/new") {
            call.receiveText()
                .let { call.respondText("Post new contents:'$it'") }
        }
        post("/update") {
            call.receiveText()
                .let { call.respondText("Post update contents:'$it'") }
        }
        post("/delete") {
            call.receiveText()
                .let { call.respondText("Post delete contents:'$it'") }
        }
    }
}