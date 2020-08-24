package com.writinggame.database

import com.writinggame.model.Game
import org.apache.ibatis.session.SqlSession

class GameBag(session: SqlSession) : PersistBag(session) {
    fun findByLobbyId(lobbyId: String): Game {
        return session.selectOne("com.writinggame.db.mappers.Game.findByLobbyId", lobbyId)
    }
}
