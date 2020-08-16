package com.writinggame.database

import com.writinggame.controller.viewModels.LobbyViewModel
import com.writinggame.model.Lobby
import org.apache.ibatis.session.SqlSession

class LobbyBag(session: SqlSession): PersistBag(session) {
    fun findCurrentLobbyState(lobbyId: String): LobbyViewModel {
        return session.selectOne("com.writinggame.db.mappers.Lobby.findCurrentLobbyState", lobbyId)
    }

    fun findPlayerNames(lobbyId: String): List<String> {
        return session.selectList("com.writinggame.db.mappers.Lobby.findPlayerNames", lobbyId)
    }
}