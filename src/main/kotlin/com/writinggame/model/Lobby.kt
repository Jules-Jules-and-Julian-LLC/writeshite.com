package com.writinggame.model

import com.writinggame.domain.LobbyStateType
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

class Lobby(val lobbyId: String, var creator: Player, settings: GameSettings) {
    val players: MutableList<Player> = mutableListOf(creator)
    val createDatetime: LocalDateTime = LocalDateTime.now()
    var lobbyState: LobbyStateType = LobbyStateType.GATHERING_PLAYERS
    var game: Game = Game(this, settings)
    val gallery: MutableList<Story> = mutableListOf()

    fun addPlayer(player: Player): Pair<Lobby, Boolean> {
        var renamedPlayer = false
        while(players.any { it.username == player.username }) {
            player.username = player.username + "2"
            renamedPlayer = true
        }

        players.add(player)
        game.addPlayer(player, lobbyState)

        if(players.size == 1) {
            creator = player
        }

        return Pair(this, renamedPlayer)
    }

    fun startGame(sessionId: String, settings: GameSettings) {
        if(isCreator(sessionId) && players.size > 1) {
            if(lobbyState == LobbyStateType.READING) {
                gallery.addAll(game.completedStories)
                lobbyState = LobbyStateType.GATHERING_PLAYERS
            } else {
                lobbyState = LobbyStateType.PLAYING
            }
            game = Game(this, settings)
        }
    }

    private fun isCreator(sessionId: String): Boolean {
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

    fun playerCanStartGame(sessionId: String): Boolean {
        return isCreator(sessionId)
    }

    fun addMessageToStory(message: String, storyId: String, sessionId: String) {
        val story = game.getStory(storyId)
        story?.addMessage(message, sessionId, game.settings)

        if(game.endTime != null) {
            val elapsedTime = Duration.between(Instant.now(), game.endTime)
            if(elapsedTime.isNegative) {
                println("Elapsed time was: ${elapsedTime.toSeconds()} seconds, so completing story: $storyId")
                completeStory(storyId, sessionId)
            }
        }
    }

    fun completeStory(storyId: String, sessionId: String) {
        val player = getPlayer(sessionId)
        if(player == null || storyNotInPlayerQueue(storyId, player)) {
            return
        }

        lobbyState = game.completeStory(storyId, player)
    }

    private fun storyNotInPlayerQueue(storyId: String, player: Player): Boolean {
        val playerStories = game.stories[player.username]
        return playerStories == null || playerStories.none { it.id == storyId }
    }

    fun getPlayerBySessionId(sessionId: String): Player? {
        return players.find { it.clientId == sessionId }
    }
}
