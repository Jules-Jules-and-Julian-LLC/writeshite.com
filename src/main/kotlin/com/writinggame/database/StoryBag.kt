package com.writinggame.database

import com.writinggame.model.Story
import org.apache.ibatis.session.SqlSession

class StoryBag(session: SqlSession): PersistBag(session) {
    fun findByGameId(gameId: Int): List<Story> {
        return session.selectOne("com.writinggame.db.mappers.Story.findByGameId", gameId)
    }
}