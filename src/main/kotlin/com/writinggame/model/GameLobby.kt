package com.writinggame.model

import com.writinggame.domain.GameStateType
import java.time.LocalDateTime

//TODO viewmodel layer, we're leaking all sorts of crap to the user by using this directly
class GameLobby(val lobbyId: String) {
    val players: MutableList<Player> = mutableListOf()
    val createDatetime: LocalDateTime = LocalDateTime.now()
    var gameState: GameStateType = GameStateType.GATHERING_PLAYERS

    fun addPlayer(player: Player) {
        if(players.any { it.username == player.username }) {
            player.username = player.username + "IsntImaginitive"
        }
        players.add(player)
    }

    fun startGame() {
        gameState = GameStateType.PLAYING
    }
}
