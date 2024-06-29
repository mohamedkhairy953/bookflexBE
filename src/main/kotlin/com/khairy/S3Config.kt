package com.khairy

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.io.File
import java.nio.file.Paths

object S3Config {
    private val awsCredentials = AwsBasicCredentials.create("AKIA6ODU47LBQH2UCDT2", "OSp4M3Wix6TU8CaEA7uDUaBU/227TZsl21iQSfo1")
    val s3Client: S3Client = S3Client.builder()
        .region(Region.EU_NORTH_1) // Change to your region
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build()
    const val BUCKET_NAME = "bookflex-bucket"
}
