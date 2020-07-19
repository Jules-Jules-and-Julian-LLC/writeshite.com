package com.writinggame.database

import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession

class PlayerBag(private val session: SqlSession) {
    fun findPlayers(): List<Player> {
        return session.selectList("selectPlayers")
    }

    fun createPlayer(lobbyId: String, username: String, sessionId: String) {
        val dbParams = object {
            val lobbyId = lobbyId
            val username = username
            val sessionId = sessionId
        }

        //todo return something
        session.insert("createPlayer", dbParams)
    }
}