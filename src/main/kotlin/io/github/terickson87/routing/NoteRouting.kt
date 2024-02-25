package io.github.terickson87.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.noteRouting() {
    route("/notes") {
        get("/all") {
            call.respondText("Get All Notes")
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
