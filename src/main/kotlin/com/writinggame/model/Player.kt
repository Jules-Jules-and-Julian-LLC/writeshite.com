package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.ibatis.session.SqlSession

//TODO player text color, error if username empty
class Player(session: SqlSession? = null, @JsonIgnore val clientId: String, var username: String, val lobbyId: String) : PersistedObject(session) {
    constructor(clientId: String, username: String, lobbyId: String) : this(null, clientId, username, lobbyId)

    override fun getKey(): Any {
        return PlayerKey(username, lobbyId)
    }
}

data class PlayerKey(val username: String, val lobbyId: String)