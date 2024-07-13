package com.khairy.models

import software.amazon.awssdk.services.s3.model.S3Object
import java.io.Serializable

@kotlinx.serialization.Serializable
data class FlexBook(
    val size: Long,
    val author: String,
    val title: String
) {
    companion object {
        fun from(s3Object: S3Object) : FlexBook {
            return FlexBook(
                title = s3Object.key(),
                author = "Mohamed Khairy",
                size = s3Object.size()
            )
        }
    }
}
