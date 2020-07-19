package com.writinggame.database

import com.writinggame.domain.LobbyStateType
import com.writinggame.model.Lobby
import org.apache.ibatis.session.SqlSession

class LobbyBag(private val session: SqlSession) {
    fun createLobbyIfNotExists(lobbyId: String) {
        val dbParams = object {
            val lobbyId = lobbyId
            val GATHERING_PLAYERS = LobbyStateType.GATHERING_PLAYERS
        }

        session.insert("createLobbyIfNotExists", dbParams);
    }

    fun find(lobbyId: String): Lobby {
        return session.selectOne("findLobby", lobbyId)
    }
}