package io.github.terickson87.plugins

import io.github.terickson87.adapter.accessor.NotesAccessor
import io.github.terickson87.routing.NoteRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(notesAccessor: NotesAccessor) {
    routing {
        val noteRouting = NoteRouting(notesAccessor)
        get("/") {
            call.respondText("Hello World!")
        }
        noteRouting.noteRouting(this)
    }
}