package com.khairy

import com.khairy.plugins.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    // Ensure the uploads directory exists
    val uploadsDir = File("uploads")
    if (!uploadsDir.exists()) {
        uploadsDir.mkdirs()
    }

    embeddedServer(Netty, port = System.getenv("PORT")?.toIntOrNull()?:8080) {
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }
        routing {
            route("/s3"){
                uploadEpub()
                getEpub()
                getAllEpub()
            }
        }
    }.start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
