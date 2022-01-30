package com.writinggame.model

import com.writinggame.domain.LobbyStateType
import java.time.LocalDateTime

class Lobby(val lobbyId: String, var creator: Player, settings: GameSettings) {
    var startingLevel: Int = 1
    val players: MutableList<Player> = mutableListOf(creator)
    val createDatetime: LocalDateTime = LocalDateTime.now()
    var lobbyState: LobbyStateType = LobbyStateType.GATHERING_PLAYERS
    var game: Game = Game(this, settings)
    var previousRoundStories: MutableList<Story> = mutableListOf()

    fun addPlayer(player: Player): Pair<Lobby, Boolean> {
        var renamedPlayer = false
        while(players.any { it.username.equals(player.username, ignoreCase = true) }) {
            player.username = player.username + "2"
            renamedPlayer = true
        }

        players.add(player)

        if(players.size == 1) {
            creator = player
        }

        return Pair(this, renamedPlayer)
    }

    fun startGame(settings: GameSettings) {
        lobbyState = LobbyStateType.PLAYING
        game = Game(this, settings)
    }

    fun isCreator(sessionId: String): Boolean {
        return sessionId == creator.clientId
    }

    fun leave(sessionId: String) {
        val player = getPlayer(sessionId)
        if(player != null) {
            players.remove(player)
        }
        if(players.isNotEmpty() && creator !in players) {
            creator = players[0]
        }
    }

    private fun getPlayer(sessionId: String): Player? {
        return players.find { it.clientId == sessionId }
    }

    fun getPlayerBySessionId(sessionId: String): Player? {
        return players.find { it.clientId == sessionId }
    }
}
