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
import java.time.ZonedDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Controller
class LobbyController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val TIMEOUT_MINUTES: Long = 1
        private val map = mutableMapOf<String, ExecutorService>()

        fun getExecutorServiceForLobby(lobbyId: String): ExecutorService {
            return map.getOrDefault(lobbyId, Executors.newSingleThreadExecutor())
        }
    }

    @MessageMapping("/lobby.{lobbyId}.joinGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun joinGame(username: String, @DestinationVariable("lobbyId") lobbyId: String,
                 @Header("simpSessionId") sessionId: String): JoinGameResponse {
        return getExecutorServiceForLobby(lobbyId).submit<JoinGameResponse> {
            val receivedDatetime = ZonedDateTime.now()
            println("Got to join lobby with lobby id: $lobbyId and username: $username and session ID: $sessionId")
            val newLobby = LobbyManager.joinLobby(username, sessionId, lobbyId)

            return@submit JoinGameResponse(newLobby, sessionId, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.startGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun startGame(@DestinationVariable("lobbyId") lobbyId: String,
                  @Header("simpSessionId") sessionId: String,
                  settings: GameSettings): StartGameResponse {
        return getExecutorServiceForLobby(lobbyId).submit<StartGameResponse> {
            val receivedDatetime = ZonedDateTime.now()
            val lobby = LobbyManager.getLobby(lobbyId)
            lobby.startGame(sessionId, settings)
            println("Starting game for lobby $lobbyId player $sessionId can start: ${lobby.playerCanStartGame(sessionId)} round time: ${settings.roundTimeMinutes}")

            return@submit StartGameResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.newMessage")
    @SendTo("/topic/lobby.{lobbyId}")
    fun newMessage(@DestinationVariable("lobbyId") lobbyId: String,
                   @Header("simpSessionId") sessionId: String,
                   receivedMessage: ReceivedMessage): StoryChangeResponse {
        return getExecutorServiceForLobby(lobbyId).submit<StoryChangeResponse> {
            val receivedDatetime = ZonedDateTime.now()
            val message = receivedMessage.message
            val storyId = receivedMessage.storyId
            val lobby = LobbyManager.getLobby(lobbyId)
            println("Adding message for lobby $lobbyId player $sessionId message $message storyId $storyId")

            lobby.addMessageToStory(message, storyId, sessionId)
            lobby.game.passStory(sessionId, storyId)

            return@submit StoryChangeResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.completeStory")
    @SendTo("/topic/lobby.{lobbyId}")
    fun completeStory(@DestinationVariable("lobbyId") lobbyId: String,
                      @Header("simpSessionId") sessionId: String,
                      storyId: String): StoryChangeResponse {
        return getExecutorServiceForLobby(lobbyId).submit<StoryChangeResponse> {
            val receivedDatetime = ZonedDateTime.now()
            val lobby = LobbyManager.getLobby(lobbyId)
            println("Completing story: $storyId for lobby: $lobbyId by user: $sessionId")

            lobby.completeStory(storyId, sessionId)

            return@submit StoryChangeResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }
}