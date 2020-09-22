package com.writinggame.model

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
        val lobby = lobbies.find { lobby -> lobby.players.any{ it.clientId == sessionId } }
        lobby?.leave(sessionId)
        cleanupEmptyLobbies()

        return lobby
    }

    private fun cleanupEmptyLobbies() {
        lobbies.filter { lobby -> lobby.players.isEmpty() }.forEach{ lobbies.remove(it) }
    }

    fun getLobby(lobbyId: String): Lobby? {
        return lobbies.find { it.lobbyId == lobbyId }
    }
}