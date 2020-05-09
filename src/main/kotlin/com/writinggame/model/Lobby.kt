package com.writinggame.model

import com.writinggame.domain.GameStateType
import java.time.LocalDateTime

//TODO viewmodel layer, we're leaking all sorts of crap to the user by using this directly
class Lobby(val lobbyId: String, creator: Player) {
    val players: HashMap<String, Player> = hashMapOf(Pair(creator.clientId, creator))
    val createDatetime: LocalDateTime = LocalDateTime.now()
    var gameState: GameStateType = GameStateType.GATHERING_PLAYERS
    private var creatorSessionId: String = creator.clientId

    fun addPlayer(player: Player): Lobby {
        while(players.values.any { it.username == player.username }) {
            player.username = player.username + "IsntImaginitive"
        }

        players[player.clientId] = player

        return this
    }

    fun startGame(sessionId: String) {
        if(isCreator(sessionId)) {
            gameState = GameStateType.PLAYING
        }
        //TODO error if not creator?
    }

    fun isCreator(sessionId: String): Boolean {
        return sessionId == creatorSessionId
    }

    fun leave(sessionId: String) {
        players.remove(sessionId)
        if(isCreator(sessionId) && players.isNotEmpty()) {
            creatorSessionId = players.values.elementAt(0).clientId
        }
    }
}
