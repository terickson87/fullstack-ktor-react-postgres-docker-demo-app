package io.github.terickson87.croprecordsservice.routing

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.domain.NewNoteRequest
import io.github.terickson87.croprecordsservice.domain.handlers.notes.GetNoteByIdHandler
import io.github.terickson87.croprecordsservice.domain.handlers.notes.CreateNoteHandler
import io.github.terickson87.croprecordsservice.domain.handlers.notes.DeleteNoteByIdHandler
import io.github.terickson87.croprecordsservice.domain.handlers.notes.IdValidator.Companion.validateCallId
import io.github.terickson87.croprecordsservice.domain.handlers.notes.UpdateNoteByIdHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class NoteRouting(private val notesAccessor: NotesAccessor) {
    private val createNoteHandler = CreateNoteHandler(notesAccessor)
    private val getNoteByIdHandler = GetNoteByIdHandler(notesAccessor)
    private val updateNoteByIdHandler = UpdateNoteByIdHandler(notesAccessor)
    private val deleteNoteByIdHandler = DeleteNoteByIdHandler(notesAccessor)

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

            post("/update/{id?}") {
                call.validateCallId { handleUpdateNoteByIdCall(call, it) }
            }

            get("/delete/{id?}") {
                call.validateCallId { handleDeleteNoteByIdCall(call, it) }
            }
        }
    }

    private suspend fun handleGetNoteByIdCall(call: ApplicationCall, id: Int): Unit =
        GetNoteByIdHandler.Input(id)
            .let { getNoteByIdHandler.handle(it) }
            .let {
                when (it) {
                    is GetNoteByIdHandler.Output.Success -> call.respond(it.noteResponse)
                    is GetNoteByIdHandler.Output.IdNotFound -> handleIdNotFound(call, id)
                }
            }

    private suspend fun handleCreateNoteCall(call: ApplicationCall): Unit =
        call.receive<NewNoteRequest>()
            .let { CreateNoteHandler.Input(it.body) }
            .let { createNoteHandler.handle(it) }
            .let {
                when(it) {
                    is CreateNoteHandler.Output.Success -> call.respond(it.noteResponse)
                    is CreateNoteHandler.Output.EmptyBody -> handleEmptyBody(call)
                }
            }

    private suspend fun handleUpdateNoteByIdCall(call: ApplicationCall, id: Int): Unit =
        call.receive<NewNoteRequest>()
            .let { UpdateNoteByIdHandler.Input(id, it.body) }
            .let { updateNoteByIdHandler.handle(it) }
            .let {
                when (it) {
                    is UpdateNoteByIdHandler.Output.Success -> call.respond(it.noteResponse)
                    is UpdateNoteByIdHandler.Output.IdNotFound -> handleIdNotFound(call, id)
                    is UpdateNoteByIdHandler.Output.EmptyBody -> handleEmptyBody(call)
                }
            }

    private suspend fun handleDeleteNoteByIdCall(call: ApplicationCall, id: Int): Unit =
        DeleteNoteByIdHandler.Input(id)
            .let { deleteNoteByIdHandler.handle(it) }
            .let {
                when (it) {
                    is DeleteNoteByIdHandler.Output.Success ->
                        call.respondText("Note ID: '${id}' was deleted successfully.",
                            status = HttpStatusCode.OK)
                    is DeleteNoteByIdHandler.Output.IdNotFound -> handleIdNotFound(call, id)
                }
            }

    private suspend fun handleIdNotFound(call: ApplicationCall, id: Int) =
        call.respondText("Note ID: '${id}' was not Found.", status = HttpStatusCode.NotFound)

    private suspend fun handleEmptyBody(call: ApplicationCall) =
        call.respondText("A non-empty body is required to create a note.", status = HttpStatusCode.BadRequest)
}


