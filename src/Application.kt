package com.parkchanwoo

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    // default header: install in our server to intercept responses with additional info in headers e.g. current date
    install(DefaultHeaders)

    // log all our http requests that come to server and responses. e.g. adding a note will add info about the note on request
    install(CallLogging)

    // define url endpoints where clients can connect to. an essential feature
    install(Routing)

    // negotiating content type that is transferred through server. must know how to respond to a received request e.g. json
    // negotiator will tell which kind of content our server will respond with
    install(ContentNegotiation) {
        // lambda function block: configure to use json as content negotiator (server expects json requests, answers in json)
        gson {
            setPrettyPrinting()
        }
    }
}

