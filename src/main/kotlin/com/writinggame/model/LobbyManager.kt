package com.writinggame.model

import com.writinggame.database.LobbyBag
import com.writinggame.database.PlayerBag
import org.apache.ibatis.session.SqlSession

object LobbyManager {
    private val lobbies: HashMap<String, Lobby> = HashMap()

    private fun createLobby(lobbyId: String, creator: Player): Lobby {
        val newLobby = Lobby(lobbyId, creator, GameSettings(), null)
        lobbies[lobbyId] = newLobby

        return newLobby
    }

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