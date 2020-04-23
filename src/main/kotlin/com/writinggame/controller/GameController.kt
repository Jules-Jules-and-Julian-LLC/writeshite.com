package com.writinggame.controller

import com.writinggame.model.GameLobby
import com.writinggame.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class GameController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    private val lobbies: List<GameLobby> = listOf()

    @MessageMapping("getClientId")
    fun getClientId(): GetClientIdResponse {
        return GetClientIdResponse(UUID.randomUUID().toString())
    }

    @MessageMapping("createGame")
    fun createGame(creator: Player): CreateGameResponse {
        println("Creating game ${creator.clientId}, ${creator.username}")
        val newLobby = GameLobby(creator)
        return CreateGameResponse(newLobby)
    }
}