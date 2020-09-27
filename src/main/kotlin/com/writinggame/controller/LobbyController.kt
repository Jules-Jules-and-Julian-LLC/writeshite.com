package com.writinggame.controller

import com.writinggame.controller.viewModels.*
import com.writinggame.domain.ErrorType
import com.writinggame.domain.LobbyStateType
import com.writinggame.model.GameSettings
import com.writinggame.model.LobbyManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.time.ZonedDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Controller
class LobbyController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var messagingTemplate: SimpMessageSendingOperations;

    private fun createHeaders(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        headerAccessor.setLeaveMutable(true)
        return headerAccessor.messageHeaders
    }

    companion object {
        const val TIMEOUT_MINUTES: Long = 2
        private val map = mutableMapOf<String, ExecutorService>()

        fun getExecutorServiceForLobby(lobbyId: String): ExecutorService {
            return map.getOrPut(lobbyId, { Executors.newSingleThreadExecutor() })
        }

        fun cleanupLobby(lobbyId: String) {
            try {
                val service = getExecutorServiceForLobby(lobbyId)
                service.shutdown()
                service.awaitTermination(TIMEOUT_MINUTES, TimeUnit.MINUTES)
            } finally {
                map.remove(lobbyId)
            }
            println("Cleaned up $lobbyId, there are now: ${map.size} active lobbies")
        }
    }

    @MessageMapping("/lobby.{lobbyId}.joinGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun joinGame(username: String, @DestinationVariable("lobbyId") lobbyId: String,
        @Header("simpSessionId") sessionId: String): Response {
        return getExecutorServiceForLobby(lobbyId).submit<Response> {
            val receivedDatetime = ZonedDateTime.now()

            val error = RequestInputValidator.validateInput(receivedDatetime, lobbyId, username)
            if (error != null) {
                return@submit error
            }

            println("Got to join lobby with lobby id: $lobbyId and username: $username and session ID: $sessionId")
            val lobbyPair = LobbyManager.joinLobby(username, sessionId, lobbyId)
            val lobby = lobbyPair.first
            val renamedPlayer = lobbyPair.second

            if (renamedPlayer) {
                val player = lobby.getPlayerBySessionId(sessionId)
                if (player != null) {
                    messagingTemplate.convertAndSendToUser(
                        sessionId, "/queue/overrideUsername",
                        player.username, createHeaders(sessionId)
                    )
                }
            }

            return@submit JoinGameResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.startGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun startGame(@DestinationVariable("lobbyId") lobbyId: String, @Header("simpSessionId") sessionId: String,
        settings: GameSettings): Response {
        return getExecutorServiceForLobby(lobbyId).submit<Response> {
            val receivedDatetime = ZonedDateTime.now()

            val error = RequestInputValidator.validateInput(receivedDatetime, lobbyId)
            if (error != null) {
                return@submit error
            }

            val lobby = LobbyManager.getLobby(lobbyId) ?: return@submit ErrorResponse(ErrorType.LOBBY_NOT_FOUND, receivedDatetime)
            if(!lobby.isCreator(sessionId)) return@submit ErrorResponse(ErrorType.NOT_LOBBY_CREATOR, receivedDatetime)
            if(lobby.players.size < 2 && lobby.lobbyState == LobbyStateType.GATHERING_PLAYERS) return@submit ErrorResponse(ErrorType.TOO_FEW_PLAYERS, receivedDatetime)

            lobby.startGame(sessionId, settings)
            println("Starting game for lobby $lobbyId player $sessionId can start: ${lobby.isCreator(sessionId)} round time: ${settings.roundTimeMinutes}")

            return@submit StartGameResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.newMessage")
    @SendTo("/topic/lobby.{lobbyId}")
    fun newMessage(@DestinationVariable("lobbyId") lobbyId: String, @Header("simpSessionId") sessionId: String,
        receivedMessage: ReceivedMessage): Response {
        return getExecutorServiceForLobby(lobbyId).submit<Response> {
            val receivedDatetime = ZonedDateTime.now()

            val message = receivedMessage.message
            val storyId = receivedMessage.storyId
            val lobby = LobbyManager.getLobby(lobbyId) ?: return@submit ErrorResponse(ErrorType.LOBBY_NOT_FOUND, receivedDatetime)
            println("Adding message for lobby $lobbyId player $sessionId message $message storyId $storyId")

            val error = RequestInputValidator.validateInput(receivedDatetime, lobbyId, message = message, settings = lobby.game.settings)
            if (error != null) {
                return@submit error
            }

            if(!lobby.canAddMessageToStory(storyId, sessionId)) {
                return@submit ErrorResponse(ErrorType.CANT_ADD_MESSAGE_TO_STORY, receivedDatetime)
            }

            lobby.addMessageToStory(message, storyId, sessionId)
            lobby.game.passStory(sessionId, storyId)

            return@submit StoryChangeResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.completeStory")
    @SendTo("/topic/lobby.{lobbyId}")
    fun completeStory(@DestinationVariable("lobbyId") lobbyId: String,
                      @Header("simpSessionId") sessionId: String, storyId: String): Response {
        return getExecutorServiceForLobby(lobbyId).submit<Response> {
            val receivedDatetime = ZonedDateTime.now()

            val error = RequestInputValidator.validateInput(receivedDatetime, lobbyId)
            if (error != null) {
                return@submit error
            }

            val lobby = LobbyManager.getLobby(lobbyId) ?: return@submit ErrorResponse(ErrorType.LOBBY_NOT_FOUND, receivedDatetime)
            println("Completing story: $storyId for lobby: $lobbyId by user: $sessionId")

            lobby.completeStory(storyId, sessionId)

            return@submit StoryChangeResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    @MessageMapping("/lobby.{lobbyId}.endRound")
    @SendTo("/topic/lobby.{lobbyId}")
    fun completeStory(@DestinationVariable("lobbyId") lobbyId: String, @Header("simpSessionId") sessionId: String): Response {
        return getExecutorServiceForLobby(lobbyId).submit<Response> {
            val receivedDatetime = ZonedDateTime.now()

            val error = RequestInputValidator.validateInput(receivedDatetime, lobbyId)
            if (error != null) {
                return@submit error
            }

            val lobby = LobbyManager.getLobby(lobbyId) ?: return@submit ErrorResponse(ErrorType.LOBBY_NOT_FOUND, receivedDatetime)
            println("Ending round for lobby: $lobbyId by user: $sessionId")

            lobby.endRound(sessionId)

            return@submit StoryChangeResponse(lobby, receivedDatetime)
        }.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
    }

    /**
     * Have to redirect react routed paths to index.html so that the react router JS can handle the routing for us.
     */
    @RequestMapping(value = ["/", "/lobby", "/lobby/*", "/gallery", "/gallery/*", "/help", "/about"])
    fun redirect(): String {
        return "forward:/index.html"
    }
}