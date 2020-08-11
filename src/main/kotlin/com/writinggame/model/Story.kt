package com.writinggame.model

import org.apache.ibatis.session.SqlSession

class Story(val id: Int, val gameId: Int, val creatorUsername: String, var editingUsername: String,
            session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return id
    }
}