package com.writinggame.database

import com.writinggame.controller.viewModels.LobbyViewModel
import com.writinggame.domain.LobbyStateType
import com.writinggame.model.Lobby
import org.apache.ibatis.session.SqlSession

class LobbyBag(private val session: SqlSession) {
    fun find(lobbyId: String): Lobby? {
        return session.selectOne("com.writinggame.db.mappers.Lobby.find", lobbyId)
    }

    fun findCurrentLobbyState(lobbyId: String): LobbyViewModel {
        return session.selectOne("com.writinggame.db.mappers.Lobby.findCurrentLobbyState", lobbyId)
    }
}