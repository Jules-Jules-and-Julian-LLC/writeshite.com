package com.writinggame.model

import org.apache.ibatis.session.SqlSession
import java.time.ZonedDateTime

class Game(val id: Int, val roundTimeMinutes: Int, val roundEndDatetime: ZonedDateTime,
           val minWordsPerMessage: Int?, var maxWordsPerMessage: Int?, val lobbyId: String,
           session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return id
    }
}