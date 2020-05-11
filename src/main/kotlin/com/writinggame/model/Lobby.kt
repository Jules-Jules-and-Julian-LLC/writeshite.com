package com.writinggame.model

import com.writinggame.domain.GameStateType
import java.time.LocalDateTime

class Lobby(val lobbyId: String, var creator: Player) {
    val players: HashMap<String, Player> = hashMapOf(Pair(creator.clientId, creator))
    val createDatetime: LocalDateTime = LocalDateTime.now()
    var gameState: GameStateType = GameStateType.GATHERING_PLAYERS
    var activeGame: Game? = null
    val finishedGames: MutableList<Game> = mutableListOf()

    fun addPlayer(player: Player): Lobby {
        while(players.values.any { it.username == player.username }) {
            player.username = player.username + "IsntImaginitive"
        }

        players[player.clientId] = player

        if(players.size == 1) {
            creator = player
        }

        return this
    }

    fun startGame(sessionId: String) {
        if(isCreator(sessionId)) {
            gameState = GameStateType.PLAYING
            activeGame = Game(this)
        }
    }

    fun isCreator(sessionId: String): Boolean {
        println("Is creator: Session ID: $sessionId, creator: ${creator.clientId}")
        return sessionId == creator.clientId
    }

    fun leave(sessionId: String) {
        players.remove(sessionId)
        if(isCreator(sessionId) && players.isNotEmpty()) {
            creator = players.values.elementAt(0)
        }
    }

    fun playerCanStartGame(sessionId: String): Boolean {
        return isCreator(sessionId)
    }
}
