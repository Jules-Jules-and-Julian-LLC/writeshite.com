package com.writinggame.model

import org.apache.ibatis.session.SqlSession

class Message(val id: Int, val storyId: Int, val creatorUsername: String, val text: String,
              session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return id
    }

}