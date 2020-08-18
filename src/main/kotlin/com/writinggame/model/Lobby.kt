package com.writinggame.model

import com.writinggame.domain.LobbyStateType
import org.apache.ibatis.session.SqlSession
import java.time.OffsetDateTime

class Lobby(val lobbyId: String, var state: LobbyStateType, var creatorUsername: String, val createDatetime: OffsetDateTime = OffsetDateTime.now(),
            var lastUpdateDatetime: OffsetDateTime = OffsetDateTime.now(), session: SqlSession?) : PersistedObject(session) {
    constructor(lobbyId: String, state: LobbyStateType, creatorUsername: String, createDatetime: OffsetDateTime, lastUpdateDatetime: OffsetDateTime)
            : this(lobbyId, state, creatorUsername, createDatetime, lastUpdateDatetime, null)
    override fun getKey(): Any {
        return lobbyId
    }
}