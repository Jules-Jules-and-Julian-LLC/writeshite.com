package com.writinggame.controller

import com.writinggame.controller.viewModels.CreateGameResponse
import com.writinggame.controller.viewModels.GetClientIdResponse
import com.writinggame.controller.viewModels.JoinGameResponse
import com.writinggame.controller.viewModels.StartGameResponse
import com.writinggame.domain.GameStateType
import com.writinggame.model.GameLobbyManager
import com.writinggame.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class GameController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    @MessageMapping("getClientId")
    fun getClientId(): GetClientIdResponse {
        return GetClientIdResponse(
            UUID.randomUUID().toString()
        )
    }

    @MessageMapping("createLobby")
    fun createLobby(): CreateGameResponse {
        return CreateGameResponse(GameLobbyManager.createLobby())
    }

    @MessageMapping("/lobby.{lobbyId}.joinGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun joinGame(username: String, @DestinationVariable("lobbyId") lobbyId: String): JoinGameResponse {
        println("Got to join lobby with lobby id: $lobbyId and username: $username")
        val newLobby = GameLobbyManager.joinLobby(username, lobbyId)

        return JoinGameResponse(newLobby)
    }

    @MessageMapping("startGame")
    fun startGame(clientId: String, lobbyId: String): StartGameResponse {
        GameLobbyManager.startGame(lobbyId)

        return StartGameResponse(GameStateType.PLAYING)
    }
}