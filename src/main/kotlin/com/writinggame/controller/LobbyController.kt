package com.writinggame.controller

import com.writinggame.controller.viewModels.*
import com.writinggame.database.LobbyBag
import com.writinggame.database.WriteShiteSessionFactory
import com.writinggame.model.GameSettings
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
                 @Header("simpSessionId") sessionId: String): LobbyViewModel {
        println("Got to join lobby with lobby id: $lobbyId and username: $username and session ID: $sessionId")
        WriteShiteSessionFactory.openSession().use { session ->
            LobbyManager.joinLobby(username, sessionId, lobbyId, session)

            session.commit()
            return LobbyBag(session).findCurrentLobbyState(lobbyId)
        }
    }

    @MessageMapping("/lobby.{lobbyId}.startGame")
    @SendTo("/topic/lobby.{lobbyId}")
    fun startGame(@DestinationVariable("lobbyId") lobbyId: String,
                  @Header("simpSessionId") sessionId: String,
                  settings: GameSettings): LobbyViewModel {
        WriteShiteSessionFactory.openSession().use { session ->
            LobbyManager.startGame(lobbyId, sessionId, settings, session)

            return LobbyBag(session).findCurrentLobbyState(lobbyId)
        }
    }

    @MessageMapping("/lobby.{lobbyId}.newMessage")
    @SendTo("/topic/lobby.{lobbyId}")
    fun newMessage(@DestinationVariable("lobbyId") lobbyId: String,
                   @Header("simpSessionId") sessionId: String,
                   receivedMessage: ReceivedMessage): LobbyViewModel {
        WriteShiteSessionFactory.openSession().use { session ->
            LobbyManager.addMessageToStory(lobbyId, receivedMessage.message, receivedMessage.storyId, sessionId, session)

            return LobbyBag(session).findCurrentLobbyState(lobbyId)
        }
    }

    @MessageMapping("/lobby.{lobbyId}.completeStory")
    @SendTo("/topic/lobby.{lobbyId}")
    fun completeStory(@DestinationVariable("lobbyId") lobbyId: String,
                      @Header("simpSessionId") sessionId: String,
                      storyId: String): LobbyViewModel {
        WriteShiteSessionFactory.openSession().use { session ->
            LobbyManager.completeStory(lobbyId, sessionId, storyId)

            return LobbyBag(session).findCurrentLobbyState(lobbyId)
        }
    }
}