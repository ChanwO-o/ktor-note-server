package com.parkchanwoo.routes

import com.parkchanwoo.data.checkPasswordForEmail
import com.parkchanwoo.data.requests.AccountRequest
import com.parkchanwoo.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch(e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            // check password
            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if (isPasswordCorrect) {
                call.respond(OK, SimpleResponse(true, "Successfully logged in"))
            } else {
                call.respond(OK, SimpleResponse(false, "The email or password is incorrect"))
            }
        }
    }
}