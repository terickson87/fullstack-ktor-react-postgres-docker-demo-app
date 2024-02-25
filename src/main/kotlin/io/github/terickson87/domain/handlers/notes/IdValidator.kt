package io.github.terickson87.domain.handlers.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class IdValidator {
    data class Input(val id: String?)

    sealed class Output {
        data class Success(val id: Int) : Output()
        data object MissingID : Output()
        data class BadId(val id: String) : Output()
    }

    companion object {
        fun validate(input: Input): Output =
            input.id?.let { validateNonNullId(it) }
                ?: Output.MissingID

        private fun validateNonNullId(id: String): Output {
            return try {
                id.toInt().let { Output.Success(it) }
            } catch (e: NumberFormatException) {
                return Output.BadId(id)
            }
        }
        suspend inline fun ApplicationCall.validateCallId(block:(id :Int) -> Unit): Unit =
            this.parameters["id"]
                .let { Input(it) }
                .let { validate(it) }
                .let {
                    when(it) {
                        is Output.BadId ->
                            this.respondText("ID '${it.id}' is not an integer", status = HttpStatusCode.BadRequest)

                        is Output.MissingID -> this.respondText("Missing ID", status = HttpStatusCode.BadRequest)
                        is Output.Success -> block(it.id)
                    }
                }
    }
}