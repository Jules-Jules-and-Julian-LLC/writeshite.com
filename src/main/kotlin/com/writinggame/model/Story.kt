package com.writinggame.model

import com.writinggame.domain.StoryStateType
import org.apache.ibatis.session.SqlSession

class Story(val id: Int, val gameId: Int, val creatorUsername: String, var editingUsername: String, var state: StoryStateType,
            session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return id
    }
}