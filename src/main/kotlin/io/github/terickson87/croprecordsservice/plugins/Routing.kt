package io.github.terickson87.croprecordsservice.plugins

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.routing.NoteRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(notesAccessor: NotesAccessor) {
    routing {
        val noteRouting = NoteRouting(notesAccessor)
        noteRouting.noteRouting(this)
    }
}