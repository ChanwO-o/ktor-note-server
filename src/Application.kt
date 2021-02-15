package com.parkchanwoo

import com.parkchanwoo.data.checkPasswordForEmail
import com.parkchanwoo.data.collections.User
import com.parkchanwoo.data.registerUser
import com.parkchanwoo.routes.loginRoute
import com.parkchanwoo.routes.noteRoutes
import com.parkchanwoo.routes.registerRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    // default header: install in our server to intercept responses with additional info in headers e.g. current date
    install(DefaultHeaders)

    // log all our http requests that come to server and responses. e.g. adding a note will add info about the note on request
    install(CallLogging)

    // negotiating content type that is transferred through server. must know how to respond to a received request e.g. json
    // negotiator will tell which kind of content our server will respond with
    install(ContentNegotiation) {
        // lambda function block: configure to use json as content negotiator (server expects json requests, answers in json)
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication) {
        configureAuth()
    }

    // define url endpoints where clients can connect to. an essential feature
    // MUST setup routes after setting up authentication (some routes require authentication)
    install(Routing) {
        // install the following routes (add each endpoint here!)
        registerRoute()
        loginRoute()
        noteRoutes()
    }
}

// extension function of Authentication.Configuration, so that we can call it above in install(Authentication)
private fun Authentication.Configuration.configureAuth() {
    // how we authenticate users. basic: not secure method, use oauth if you want secure auth
    basic {
        realm = "Ye Notes" // server title
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password
            if (checkPasswordForEmail(email, password)) {
                // UserIdPrincipal: a type to keep track of who's auth or not. attached to request later on
                UserIdPrincipal(email)
            } else null
        }
    }
}