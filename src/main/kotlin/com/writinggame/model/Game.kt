package com.writinggame.model

import org.apache.ibatis.session.SqlSession
import java.time.OffsetDateTime

class Game(val id: Int, val roundTimeMinutes: Int?, val roundEndDatetime: OffsetDateTime? = null,
           val minWordsPerMessage: Int?, var maxWordsPerMessage: Int?, val lobbyId: String,
           session: SqlSession?) : PersistedObject(session) {
    //TODO this sucks, but no idea how else to make Mybatis happy
    constructor(id: Int, roundTimeMinutes: Int?, roundEndDatetime: OffsetDateTime? = null,
               minWordsPerMessage: Int?, maxWordsPerMessage: Int?, lobbyId: String)
            : this(id, roundTimeMinutes, roundEndDatetime, minWordsPerMessage, maxWordsPerMessage, lobbyId, null)
    override fun getKey(): Any {
        return id
    }
}