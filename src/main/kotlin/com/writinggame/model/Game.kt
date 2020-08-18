package com.writinggame.model

import org.apache.ibatis.session.SqlSession
import java.time.OffsetDateTime

class Game(val id: Int, val roundTimeMinutes: Int?, val roundEndDatetime: OffsetDateTime?,
           val minWordsPerMessage: Int?, var maxWordsPerMessage: Int?, val lobbyId: String,
           session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return id
    }
}