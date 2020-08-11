package com.writinggame.controller

import com.writinggame.database.LobbyBag
import com.writinggame.model.GameSettings
import com.writinggame.model.Lobby
import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession

object LobbyManager {
    fun joinLobby(username: String, sessionId: String, lobbyId: String, session: SqlSession): Lobby {
        val player = Player(session, clientId = sessionId, username = username, lobbyId = lobbyId)
        var lobby = LobbyBag(session).find(lobbyId)
        if(lobby == null) {
            lobby = Lobby(lobbyId, player, GameSettings(), session).save()
        }
        //TODO fix the type hinting
        //Can't save until the lobby exists
        player.save<Player>()

        return lobby
    }

    fun leaveLobby(sessionId: String) {
        TODO("Not yet implemented")
    }

    fun startGame(lobbyId: String, sessionId: String, settings: GameSettings): Any {
        println("Starting game for lobby $lobbyId player $sessionId can start: ${lobby.playerCanStartGame(sessionId)} round time: ${settings.roundTimeMinutes}")
        TODO("Not yet implemented")
    }

    fun addMessageToStory(lobbyId: String, message: String, storyId: String, sessionId: String): Any {
        println("Adding message for lobby $lobbyId player $sessionId message $message storyId $storyId")
        TODO("Not yet implemented")
    }

    fun completeStory(lobbyId: String, sessionId: String, storyId: String) {
        println("Completing story: $storyId for lobby: $lobbyId by user: $sessionId")
        TODO("Not yet implemented")
    }
}