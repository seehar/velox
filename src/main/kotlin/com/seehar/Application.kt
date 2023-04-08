package com.seehar

import io.ktor.server.application.*
import com.seehar.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    // configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
