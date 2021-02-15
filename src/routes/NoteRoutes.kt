package com.parkchanwoo.routes

import com.parkchanwoo.data.*
import com.parkchanwoo.data.collections.Note
import com.parkchanwoo.data.requests.AddOwnerRequest
import com.parkchanwoo.data.requests.DeleteNoteRequest
import com.parkchanwoo.data.responses.SimpleResponse
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
                val email = call.principal<UserIdPrincipal>()!!.name // not-not: we can be sure that a principal is attached (not null)

                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }

    route("/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (!checkIfUserExists(request.owner)) { // no owner with that email exists
                    call.respond(
                        OK,
                        SimpleResponse(false, "No user with this email exists")
                    )
                    return@post
                }
                if (isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(
                        OK,
                        SimpleResponse(false, "This user is already an owner of this note")
                    )
                    return@post
                }
                if (addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(
                        OK,
                        SimpleResponse(true, "${request.owner} can now see this note")
                    )
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (deleteNoteForUser(email, request.id)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
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