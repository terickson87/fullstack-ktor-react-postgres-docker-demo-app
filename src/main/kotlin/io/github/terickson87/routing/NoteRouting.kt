package io.github.terickson87.routing

import io.github.terickson87.adapter.accessor.NotesAccessor
import io.github.terickson87.domain.NoteRequest
import io.github.terickson87.domain.handlers.notes.GetNoteByIdHandler
import io.github.terickson87.domain.handlers.notes.CreateNoteHandler
import io.github.terickson87.domain.handlers.notes.IdValidator.Companion.validateCallId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class NoteRouting(private val notesAccessor: NotesAccessor) {
    private val createNoteHandler = CreateNoteHandler(notesAccessor)
    private val getNoteByIdHandler = GetNoteByIdHandler(notesAccessor)

    fun noteRouting(parentRoute: Route) = parentRoute {
        route("/notes") {
            get("/all") {
                call.respondText("Get All Notes")
            }
            get("/{id?}") {
                call.validateCallId { handleGetNoteByIdCall(call, it) }
            }

            post("/new") {
                handleCreateNoteCall(call)
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

    private suspend fun handleGetNoteByIdCall(call: ApplicationCall, id: Int): Unit =
        GetNoteByIdHandler.Input(id)
            .let { getNoteByIdHandler.handle(it) }
            .let {
                when (it) {
                    is GetNoteByIdHandler.Output.Success -> call.respond(it.noteResponse)
                    is GetNoteByIdHandler.Output.IdNotFound ->
                        call.respondText("ID '${it.id}' was not Found", status = HttpStatusCode.NotFound)
                }
            }

    private suspend fun handleCreateNoteCall(call: ApplicationCall): Unit =
        call.receive<NoteRequest>()
            .let { CreateNoteHandler.Input(it.body) }
            .let { createNoteHandler.handle(it) }
            .let {
                when(it) {
                    is CreateNoteHandler.Output.Success -> call.respond(it.noteResponse)
                    is CreateNoteHandler.Output.EmptyBody ->
                        call.respondText("A non-empty body is required to create a note",
                            status = HttpStatusCode.BadRequest)
                }
            }
}


