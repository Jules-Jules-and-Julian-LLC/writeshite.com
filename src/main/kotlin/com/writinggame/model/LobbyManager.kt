package com.writinggame.model

import java.time.ZonedDateTime

object LobbyManager {
    private val lobbies: HashMap<String, Lobby> = HashMap()

    private fun createLobby(lobbyId: String, creator: Player): Lobby {
        val newLobby = Lobby(lobbyId, creator)
        lobbies[lobbyId] = newLobby

        return newLobby
    }

    fun joinLobby(username: String, sessionId: String, lobbyId: String): Lobby {
        //TODO technical exception
        val player = Player(sessionId, username)
        return if(!lobbyExists(lobbyId)) {
            createLobby(lobbyId, player)
        } else {
            getLobby(lobbyId).addPlayer(player)
        }
    }

    fun leaveLobby(sessionId: String) {
        lobbies.values.forEach { it.leave(sessionId) }
    }

    private fun lobbyExists(lobbyId: String): Boolean {
        return lobbies[lobbyId] != null
    }

    fun getLobby(lobbyId: String): Lobby {
        return lobbies[lobbyId] ?: throw Exception("missing lobby")
    }

    fun closeAllEmptyLobbies() {
        lobbies.entries.removeIf{ it.value.players.isEmpty() }
    }

    fun closeLobbiesOlderThan(date: ZonedDateTime) {
        throw NotImplementedError()
    }
}