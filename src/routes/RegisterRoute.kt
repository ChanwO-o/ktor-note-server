package com.parkchanwoo.routes

import com.parkchanwoo.data.checkIfUserExists
import com.parkchanwoo.data.collections.User
import com.parkchanwoo.data.registerUser
import com.parkchanwoo.data.requests.AccountRequest
import com.parkchanwoo.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

// extension function of Route
fun Route.registerRoute() {
    route("/register") { // listen to incoming http requests to this route
        post { // client will be sending a post request to post user data
            val request = try { // try to parse request into variable request
                call.receive<AccountRequest>() // type of request: AccountRequest
            } catch(e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest) // notify sender that this request is invalid
                return@post // return out of the post function
            }
            val userExists = checkIfUserExists(request.email)
            if (!userExists) {
                if (registerUser(User(request.email, request.password))) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Successfully created account!"))
                } else {
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "An unknown error occurred."))
                }
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "A user with that E-Mail already exists."))
            }
        }
    }
}