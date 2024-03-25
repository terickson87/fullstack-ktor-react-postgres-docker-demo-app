package io.github.terickson87.ktorpostgresdemo.plugins

import io.github.terickson87.ktorpostgresdemo.adapter.accessor.NotesAccessor
import io.github.terickson87.ktorpostgresdemo.routing.NoteRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(notesAccessor: NotesAccessor) {
    routing {
        val noteRouting = NoteRouting(notesAccessor)
        noteRouting.noteRouting(this)
        singlePageApplication {
            react("website")
            // you need to copy the files over because they aren't being copied to the docker container
        }
    }
}