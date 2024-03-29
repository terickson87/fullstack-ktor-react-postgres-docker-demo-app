package io.github.terickson87.ktorpostgresdemo.domain

import io.github.terickson87.ktorpostgresdemo.adapter.accessor.Note

fun Note.toNoteResponse(): NoteResponse =
    NoteResponse(this.id, this.createdAt, this.updatedAt, this.body)