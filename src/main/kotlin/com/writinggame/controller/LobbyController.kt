package com.writinggame.controller

import com.writinggame.controller.viewModels.*
import com.writinggame.model.GameSettings
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
                  @Header("simpSessionId") sessionId: String,
                  settings: GameSettings): StartGameResponse {
        val lobby = LobbyManager.getLobby(lobbyId)
        lobby.startGame(sessionId, settings)
        println("Starting game for lobby $lobbyId player $sessionId can start: ${lobby.playerCanStartGame(sessionId)} round time: ${settings.roundTimeMinutes}")

        return StartGameResponse(lobby)
    }

    @MessageMapping("/lobby.{lobbyId}.newMessage")
    @SendTo("/topic/lobby.{lobbyId}")
    fun newMessage(@DestinationVariable("lobbyId") lobbyId: String,
                   @Header("simpSessionId") sessionId: String,
                   receivedMessage: ReceivedMessage): StoryChangeResponse {
        val message = receivedMessage.message
        val storyId = receivedMessage.storyId
        val lobby = LobbyManager.getLobby(lobbyId)
        println("Adding message for lobby $lobbyId player $sessionId message $message storyId $storyId")

        lobby.addMessageToStory(message, storyId, sessionId)
        lobby.game.passStory(sessionId, storyId)

        return StoryChangeResponse(lobby)
    }

    @MessageMapping("/lobby.{lobbyId}.completeStory")
    @SendTo("/topic/lobby.{lobbyId}")
    fun completeStory(@DestinationVariable("lobbyId") lobbyId: String,
                      @Header("simpSessionId") sessionId: String,
                      storyId: String): StoryChangeResponse {
        val lobby = LobbyManager.getLobby(lobbyId)
        println("Completing story: $storyId for lobby: $lobbyId by user: $sessionId")

        lobby.completeStory(storyId, sessionId)

        return StoryChangeResponse(lobby)
    }
}