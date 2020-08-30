package com.writinggame.model

object LobbyManager {
    private val lobbies: HashMap<String, Lobby> = HashMap()

    private fun createLobby(lobbyId: String, creator: Player): Lobby {
        val newLobby = Lobby(lobbyId, creator, GameSettings())
        lobbies[lobbyId] = newLobby

        return newLobby
    }

    /**
     * @return Pair of the updated lobby and whether the added player was renamed or not.
     */
    fun joinLobby(username: String, sessionId: String, lobbyId: String): Pair<Lobby, Boolean> {
        val player = Player(sessionId, username)

        return if(!lobbyExists(lobbyId)) {
            Pair(createLobby(lobbyId, player), false)
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