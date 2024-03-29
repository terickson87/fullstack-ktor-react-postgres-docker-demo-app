package io.github.terickson87.ktorpostgresdemo.adapter.accessor

import java.time.Instant

data class PagedData<T,C>(val values: List<T>,  val continuation: C? = null)
data class Note(val id: Int, val createdAt: Instant, val updatedAt: Instant, val body: String)