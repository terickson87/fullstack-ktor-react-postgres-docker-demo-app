package io.github.terickson87.domain

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(val body: String)

@Serializable
data class NoteResponse(val id: Long, val createdAt: Long, val body: String)

@Serializable
data class ErrorResponse(val message: String)