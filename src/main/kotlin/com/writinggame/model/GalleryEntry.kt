package com.writinggame.model

import org.apache.ibatis.session.SqlSession

class GalleryEntry(val id: Int, val creatorUsername: String, val lobbyId: String, val roundNumber: Int, val storyText: String,
                   session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return id
    }
}