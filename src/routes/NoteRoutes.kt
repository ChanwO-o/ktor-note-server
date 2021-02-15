package com.parkchanwoo.routes

import com.parkchanwoo.data.getNotesForUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route("/getNotes") {
        // whoever makes request for notes must be authenticated
        authenticate {
            // get request for notes
            get {
                val email = call.principal<UserIdPrincipal>()!!.name // not-not: we can be sure that a principal is attached

                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }
}