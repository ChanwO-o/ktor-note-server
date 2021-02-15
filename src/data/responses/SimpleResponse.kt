package com.parkchanwoo.data.responses

// describes a simple response that tells whether a request was successful or not
data class SimpleResponse(
    val successful: Boolean,
    val message: String
)