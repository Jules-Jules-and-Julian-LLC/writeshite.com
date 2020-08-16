package com.writinggame.database

import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession

class PlayerBag(session: SqlSession): PersistBag(session) {
    fun findByClientId(clientId: String): Player {
        return session.selectOne("com.writinggame.db.mappers.Player.findByClientId", clientId)
    }
}