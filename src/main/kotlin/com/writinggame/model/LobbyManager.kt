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
        val existingLobby = getLobby(lobbyId)

        return existingLobby?.addPlayer(player) ?: Pair(createLobby(lobbyId, player), false)
    }

    fun leaveLobby(sessionId: String) {
        lobbies.values.forEach { it.leave(sessionId) }
        cleanupEmptyLobbies()
    }

    private fun cleanupEmptyLobbies() {
        val emptyLobbies = lobbies.keys.filter { lobbyId -> getLobby(lobbyId)?.players?.isEmpty() ?: false }
        emptyLobbies.forEach{ lobbies.remove(it) }
    }

    fun getLobby(lobbyId: String): Lobby? {
        return lobbies[lobbyId]
    }
}