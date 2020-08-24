package com.writinggame.database

import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession

class PlayerBag(session: SqlSession): PersistBag(session) {
    fun findByClientId(clientId: String): Player {
        return session.selectOne("com.writinggame.db.mappers.Player.findByClientId", clientId)
    }

    fun findByLobbyId(lobbyId: String): List<Player> {
        return session.selectList("com.writinggame.db.mappers.Player.findByLobbyId", lobbyId)
    }
}