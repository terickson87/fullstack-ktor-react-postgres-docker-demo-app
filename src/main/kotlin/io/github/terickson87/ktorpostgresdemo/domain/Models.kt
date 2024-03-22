@file:UseSerializers(InstantSerializer::class)

package io.github.terickson87.ktorpostgresdemo.domain

import io.github.terickson87.ktorpostgresdemo.plugins.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class NewNoteRequest(val body: String)

@Serializable
data class NoteResponse(val id: Int, val createdAt: Instant, val modifiedAt: Instant, val body: String)

@Serializable
data class PageInfo<T,C>(val pageSize: T? = null, val continuation: C? = null)

@Serializable
data class AllNotesResponsePage(val notes: List<NoteResponse>, val pageInfo: PageInfo<Int, Long>)

@Serializable
data class ErrorResponse(val message: String)

data class Note(val id: Int, val createdAt: Instant, val updatedAt: Instant, val body: String)