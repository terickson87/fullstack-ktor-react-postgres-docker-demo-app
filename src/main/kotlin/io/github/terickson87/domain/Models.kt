@file:UseSerializers(InstantSerializer::class)

package io.github.terickson87.domain

import io.github.terickson87.plugins.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class NoteRequest(val body: String)

@Serializable
data class NoteResponse(val id: Int, val createdAt: Instant, val modifiedAt: Instant, val body: String)

@Serializable
data class ErrorResponse(val message: String)

data class Note(val id: Int, val createdAt: Instant, val updatedAt: Instant, val body: String)