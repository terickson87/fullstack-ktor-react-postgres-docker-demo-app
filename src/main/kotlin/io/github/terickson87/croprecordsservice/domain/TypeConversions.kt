package io.github.terickson87.croprecordsservice.domain

fun Note.toNoteResponse(): NoteResponse =
    NoteResponse(this.id, this.createdAt, this.updatedAt, this.body)