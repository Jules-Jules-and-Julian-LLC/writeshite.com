package com.writinggame.model

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object GalleryManager {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()
    private val s3Client = initAmazonS3Client()

    private fun initAmazonS3Client(): AmazonS3 {
        val rawCreds: AwsCredentials = jsonObjectMapper.readValue(File("secrets/aws_creds.json"))
        val credential = BasicAWSCredentials(rawCreds.accessKey, rawCreds.secretKey)

        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(rawCreds.endpoint, "us-west-002"))
            .withCredentials(AWSStaticCredentialsProvider(credential)).build()
    }

    private fun loadFromFile(lobbyId: String) : Gallery {
        if(s3Client.doesObjectExist("write-shite-galleries", "$lobbyId.json")) {
            val existingFile = s3Client.getObject("write-shite-galleries", "$lobbyId.json")
            return jsonObjectMapper.readValue(existingFile.objectContent)
        }

        return Gallery(lobbyId, mutableListOf())
    }

    fun addStoriesToGallery(lobbyId: String, stories: List<Story>) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val galleryToSave = loadFromFile(lobbyId)
                galleryToSave.addStories(stories)
                //Warning is not true, because of withContext(Dispatchers.IO). Technically, it blocks but it'll make new threads as needed
                s3Client.putObject("write-shite-galleries", "$lobbyId.json", jsonObjectMapper.writeValueAsString(galleryToSave))
            }
        }
    }
}