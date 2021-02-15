package com.parkchanwoo.routes

import com.parkchanwoo.data.collections.Note
import com.parkchanwoo.data.getNotesForUser
import com.parkchanwoo.data.saveNote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route("/getNotes") {
        // whoever makes request for notes must be authenticated (only currently logged in users can get notes)
        authenticate {
            // get request for notes
            get {
                val email = call.principal<UserIdPrincipal>()!!.name // not-not: we can be sure that a principal is attached

                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }

    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>() // parse request to a note type
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (saveNote(note)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
}