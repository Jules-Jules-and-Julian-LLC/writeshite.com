package com.writinggame.model

import java.time.LocalDateTime
import java.util.*

class GameLobby(val creator: Player, val lobbyId: String) {
    val players: MutableList<Player> = mutableListOf(creator)
    val createDatetime: LocalDateTime = LocalDateTime.now()

    fun addPlayer(player: Player) {
        players.add(player)
    }
}
