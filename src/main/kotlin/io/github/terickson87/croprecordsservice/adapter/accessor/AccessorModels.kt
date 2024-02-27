package io.github.terickson87.croprecordsservice.adapter.accessor

data class PagedData<T,C>(val values: List<T>,  val continuation: C?)