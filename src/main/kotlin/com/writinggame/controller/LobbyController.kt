package com.writinggame.controller

import com.writinggame.controller.viewModels.*
import com.writinggame.model.LobbyManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class LobbyController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    @MessageMapping("/lobby.{lobbyId}.joinGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun joinGame(username: String, @DestinationVariable("lobbyId") lobbyId: String,
                 @Header("simpSessionId") sessionId: String): JoinGameResponse {
        println("Got to join lobby with lobby id: $lobbyId and username: $username and session ID: $sessionId")
        val newLobby = LobbyManager.joinLobby(username, sessionId, lobbyId)

        return JoinGameResponse(newLobby, sessionId)
    }

    @MessageMapping("/lobby.{lobbyId}.startGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun startGame(@DestinationVariable("lobbyId") lobbyId: String,
                  @Header("simpSessionId") sessionId: String): StartGameResponse {
        val lobby = LobbyManager.getLobby(lobbyId)
        lobby.startGame(sessionId)
        println("Starting game for lobby $lobbyId player $sessionId can start: ${lobby.playerCanStartGame(sessionId)}")

        return StartGameResponse(lobby)
    }

    @MessageMapping("/lobby.{lobbyId}.newMessage")
    @SendTo("/topic/lobby.{lobbyId}")
    fun newMessage(@DestinationVariable("lobbyId") lobbyId: String,
                   @Header("simpSessionId") sessionId: String,
                   receivedMessage: ReceivedMessage): NewMessageResponse {
        val message = receivedMessage.message
        val storyId = receivedMessage.storyId
        val lobby = LobbyManager.getLobby(lobbyId)
        println("Adding message for lobby $lobbyId player $sessionId message $message storyId $storyId")

        lobby.addMessageToStory(message, storyId, sessionId)
        lobby.game.passStory(storyId)

        return NewMessageResponse(lobby.game)
    }
}