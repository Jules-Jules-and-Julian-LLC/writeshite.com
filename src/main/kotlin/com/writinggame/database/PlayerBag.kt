package com.writinggame.database

import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession

class PlayerBag(private val session: SqlSession) {
    fun findPlayers(): List<Player> {
        return session.selectList("com.writinggame.db.mappers.Player.selectPlayers")
    }
}