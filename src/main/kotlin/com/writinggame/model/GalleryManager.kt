package com.writinggame.model

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import java.util.*

private const val BUCKET_NAME = "write-shite-galleries"

object GalleryManager {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()
    private val s3Client = initAmazonS3Client()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun initAmazonS3Client(): AmazonS3 {
        val accessKey = System.getenv("B2_ACCESS_KEY") ?: throw IllegalStateException("B2_ACCESS_KEY is not set")
        val secretKey = System.getenv("B2_SECRET_KEY") ?: throw IllegalStateException("B2_SECRET_KEY is not set")
        val endpoint = System.getenv("B2_ENDPOINT") ?: throw IllegalStateException("B2_ENDPOINT is not set")

        val credentials = BasicAWSCredentials(accessKey, secretKey)

        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, "us-west-002"))
            .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
    }

    fun loadFromFile(lobbyId: String): Gallery {
        if (s3Client.doesObjectExist(BUCKET_NAME, "${lobbyId.uppercase(Locale.getDefault())}.json")) {
            val existingFile = s3Client.getObject(
                BUCKET_NAME,
                "${lobbyId.uppercase(Locale.getDefault())}.json")
            return jsonObjectMapper.readValue(existingFile.objectContent)
        }

        return Gallery(lobbyId, mutableListOf())
    }

    fun addStoriesToGallery(lobbyId: String, stories: List<Story>) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val galleryToSave = loadFromFile(lobbyId)
                println("Loaded gallery for ${lobbyId} successfully")
                galleryToSave.addStories(stories)
                println("Added stories to ${lobbyId} successfully")
                s3Client.putObject(
                    BUCKET_NAME,
                    "${lobbyId.uppercase(Locale.getDefault())}.json", jsonObjectMapper.writeValueAsString(galleryToSave))
                println("Put to ${lobbyId} bucket successful")
            }
        }
    }
}