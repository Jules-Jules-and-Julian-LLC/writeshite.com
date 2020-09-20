package com.writinggame.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

object GalleryManager {
    private const val GALLERIES_DIRECTORY = "src/main/resources/static/galleries"
    private val objectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()

    private fun loadFromFile(lobbyId: String) : Gallery {
        val directory = File(GALLERIES_DIRECTORY)
        if(!directory.exists()) {
            directory.mkdir()
        }
        val file = getFileForLobby(lobbyId)
        if(!file.exists()) {
            file.createNewFile()
        }
        return objectMapper.readValue(file)
    }

    fun addStoriesToGallery(lobbyId: String, stories: List<Story>) {
        val galleryToSave = loadFromFile(lobbyId)
        galleryToSave.addStories(stories)
        objectMapper.writeValue(getFileForLobby(lobbyId), galleryToSave)
    }

    private fun getFileForLobby(lobbyId: String) : File {
        return File("$GALLERIES_DIRECTORY/$lobbyId.json")
    }
}