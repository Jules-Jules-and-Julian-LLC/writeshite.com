package com.writinggame.model

import com.writinggame.controller.LobbyController

object LobbyManager {
    private val lobbies: MutableList<Lobby> = mutableListOf()

    private fun createLobby(lobbyId: String, creator: Player): Lobby {
        val newLobby = Lobby(lobbyId, creator, GameSettings())
        lobbies.add(newLobby)

        return newLobby
    }

    /**
     * @return Pair of the updated lobby and whether the added player was renamed or not.
     */
    fun joinLobby(username: String, sessionId: String, lobbyId: String): Pair<Lobby, Boolean> {
        val player = Player(sessionId, username)
        val existingLobby = getLobby(lobbyId)

        return existingLobby?.addPlayer(player) ?: Pair(createLobby(lobbyId, player), false)
    }

    fun leaveLobby(sessionId: String): Lobby? {
        val lobby = lobbies.find { lobby -> lobby.getPlayerBySessionId(sessionId) != null }
        lobby?.leave(sessionId)
        cleanupEmptyLobbies()

        return lobby
    }

    private fun cleanupEmptyLobbies() {
        val emptyLobbies = lobbies.filter { lobby -> lobby.players.isEmpty() || lobby.game.stories.isEmpty() }
        emptyLobbies.forEach{ lobby ->
            LobbyController.cleanupLobby(lobby.lobbyId)
            lobbies.remove(lobby)
        }
    }

    fun getLobby(lobbyId: String): Lobby? {
        return lobbies.find { it.lobbyId == lobbyId }
    }
}