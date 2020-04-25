package com.writinggame.controller

import com.writinggame.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class GameController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    @MessageMapping("getClientId")
    fun getClientId(): GetClientIdResponse {
        return GetClientIdResponse(UUID.randomUUID().toString())
    }

    @MessageMapping("createGameLobby")
    fun createGameLobby(creator: Player): CreateGameResponse {
        val newLobby = GameLobbyManager.createLobby(creator)
        println("Created game lobby ${creator.clientId}, ${creator.username}, lobby ID: ${newLobby.lobbyId}")

        return CreateGameResponse(newLobby)
    }
}