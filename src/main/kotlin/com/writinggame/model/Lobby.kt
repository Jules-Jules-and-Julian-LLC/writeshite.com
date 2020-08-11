package com.writinggame.model

import com.writinggame.domain.LobbyStateType
import org.apache.ibatis.session.SqlSession
import java.time.ZonedDateTime

class Lobby(val lobbyId: String, var lobbyState: LobbyStateType, val createDateTime: ZonedDateTime = ZonedDateTime.now(),
            var lastUpdateDatetime: ZonedDateTime = ZonedDateTime.now(), session: SqlSession?) : PersistedObject(session) {
    override fun getKey(): Any {
        return lobbyId
    }
}