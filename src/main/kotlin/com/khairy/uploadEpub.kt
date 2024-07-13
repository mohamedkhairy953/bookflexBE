package com.khairy

import com.khairy.models.FlexBook
import io.ktor.http.content.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.io.File
import java.nio.file.Paths

fun Route.uploadEpub() {
    post("/upload") {
        val multipart = call.receiveMultipart()
        var fileDescription = ""
        var fileName = ""

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "description") {
                        fileDescription = part.value
                    }
                }

                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    val tempFile = File.createTempFile("upload-", fileName)
                    tempFile.writeBytes(fileBytes)

                    // Upload to S3
                    val putRequest = PutObjectRequest.builder()
                        .bucket(S3Config.BUCKET_NAME)
                        .key(fileName)
                        .build()
                    S3Config.s3Client.putObject(putRequest, Paths.get(tempFile.absolutePath))

                    // Clean up temporary file
                    tempFile.delete()
                }

                else -> Unit
            }
            part.dispose()
        }
        call.respondText(
            "File uploaded successfully: $fileName with description: $fileDescription",
            status = HttpStatusCode.OK
        )
    }
}

fun Route.getEpub() {
    get("/epub/{fileName}") {
        val fileName = call.parameters["fileName"]
        if (fileName != null) {
            try {
                // Retrieve from S3
                val getRequest = GetObjectRequest.builder()
                    .bucket(S3Config.BUCKET_NAME)
                    .key(fileName)
                    .build()
                val s3Object = S3Config.s3Client.getObject(getRequest)

                call.respondOutputStream(contentType = ContentType.Application.OctetStream) {
                    s3Object.use { inputStream ->
                        inputStream.copyTo(this)
                    }
                }
            } catch (e: S3Exception) {
                call.respondText("File not found", status = HttpStatusCode.NotFound)
            }
        } else {
            call.respondText("Missing file name", status = HttpStatusCode.BadRequest)
        }
    }
}

fun Route.getAllEpub() {
    get("/epub/list") {
        try {
            // Retrieve from S3
            val getRequest = ListObjectsV2Request.builder()
                .bucket(S3Config.BUCKET_NAME)
                .build()
            val listResponse = S3Config.s3Client.listObjectsV2(getRequest)
            val books = listResponse.contents().map { FlexBook.from(it) }.toTypedArray()
            call.respond(books)
        } catch (e: S3Exception) {
            call.respondText(e.message ?: e.localizedMessage, status = HttpStatusCode.NotFound)
        }
    }
}

