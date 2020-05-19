package com.writinggame.model

import com.writinggame.domain.GameStateType
import java.time.LocalDateTime

class Lobby(val lobbyId: String, var creator: Player) {
    val players: MutableList<Player> = mutableListOf(creator)
    val createDatetime: LocalDateTime = LocalDateTime.now()
    var gameState: GameStateType = GameStateType.GATHERING_PLAYERS
    var game: Game = Game(this)

    fun addPlayer(player: Player): Lobby {
        while(players.any { it.username == player.username }) {
            player.username = player.username + " Isn't Imaginative"
        }

        players.add(player)
        game.addPlayer(player, gameState)

        if(players.size == 1) {
            creator = player
        }

        return this
    }

    fun startGame(sessionId: String) {
        if(isCreator(sessionId)) {
            gameState = GameStateType.PLAYING
            game = Game(this)
        }
    }

    fun isCreator(sessionId: String): Boolean {
        return sessionId == creator.clientId
    }

    fun leave(sessionId: String) {
        val player = getPlayer(sessionId)
        if(player != null) {
            players.remove(player)
            if(creator == player && players.isNotEmpty()) {
                creator = players[0]
            }
        }
    }

    private fun getPlayer(sessionId: String): Player? {
        return players.find { it.clientId == sessionId }
    }

    fun playerCanStartGame(sessionId: String): Boolean {
        return isCreator(sessionId)
    }

    fun addMessageToStory(message: String, storyId: String, creatorSessionId: String) {
        val story = game.getStory(storyId)
        story.addMessage(message, creatorSessionId)
    }
}
