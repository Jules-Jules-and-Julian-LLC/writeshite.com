package com.writinggame.model

object LobbyManager {
    private val lobbies: HashMap<String, Lobby> = HashMap()

    private fun createLobby(lobbyId: String, creator: Player): Lobby {
        val newLobby = Lobby(lobbyId, creator)
        lobbies[lobbyId] = newLobby

        return newLobby
    }

    fun joinLobby(username: String, sessionId: String, lobbyId: String): Lobby {
        val player = Player(sessionId, username)
        return if(!lobbyExists(lobbyId)) {
            createLobby(lobbyId, player)
        } else {
            getLobby(lobbyId).addPlayer(player)
        }
    }

    fun leaveLobby(sessionId: String) {
        lobbies.values.forEach { it.leave(sessionId) }
        cleanupEmptyLobbies()
    }

    private fun cleanupEmptyLobbies() {
        val emptyLobbies = lobbies.keys.filter { lobbyId -> getLobby(lobbyId).players.isEmpty() }
        emptyLobbies.forEach{ lobbies.remove(it) }
    }

    private fun lobbyExists(lobbyId: String): Boolean {
        return lobbies[lobbyId] != null
    }

    fun getLobby(lobbyId: String): Lobby {
        return lobbies[lobbyId] ?: throw Exception("missing lobby")
    }
}