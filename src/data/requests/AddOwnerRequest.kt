package com.parkchanwoo.data.requests

data class AddOwnerRequest(
    val noteId: String, // id of note
    val owner: String // owner to add
)