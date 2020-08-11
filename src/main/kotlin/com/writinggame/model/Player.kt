package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.ibatis.session.SqlSession

//TODO player text color, error if username empty
class Player(@JsonIgnore val clientId: String, var username: String, val lobbyId: String, session: SqlSession? = null) : PersistedObject(session) {
    constructor(clientId: String, username: String, lobbyId: String) : this(clientId, username, lobbyId, null)

    override fun getKey(): Any {
        return PlayerKey(username, lobbyId)
    }
}

data class PlayerKey(val username: String, val lobbyId: String)