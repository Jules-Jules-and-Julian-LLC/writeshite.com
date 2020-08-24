package com.writinggame.database

import com.writinggame.model.Message
import org.apache.ibatis.session.SqlSession

class MessageBag(session: SqlSession) : PersistBag(session) {
    fun findByStoryId(storyId: Int): List<Message> {
        return session.selectList("com.writinggame.db.mappers.Message.findByStoryId", storyId)
    }
}
