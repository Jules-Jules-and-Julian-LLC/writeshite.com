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
    var previousRoundStories: MutableList<Story> = mutableListOf()

    fun addPlayer(player: Player): Pair<Lobby, Boolean> {
        var renamedPlayer = false
        while(players.any { it.username.lowercase() == player.username.lowercase() }) {
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

    fun startGame(settings: GameSettings) {
        if(lobbyState == LobbyStateType.READING) {
            previousRoundStories = game.completedStories
            lobbyState = LobbyStateType.GATHERING_PLAYERS
        } else {
            lobbyState = LobbyStateType.PLAYING
        }
        game = Game(this, settings)
    }

    private fun unused() {
        println();
    }

    private fun addCompletedStoriesToGallery() {
        if(game.settings.saveStoriesToGallery) {
            GalleryManager.addStoriesToGallery(lobbyId, game.completedStories)
        }
    }


    fun isCreator(sessionId: String): Boolean {
        return sessionId == creator.clientId
    }

    fun leave(sessionId: String) {
        game.removePlayer(sessionId)
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

    fun addMessageToStory(message: String, storyId: String, sessionId: String) {
        if(game.endTime != null && Duration.between(Instant.now(), game.endTime).isNegative) {
            completeStory(storyId, sessionId, message)
        } else {
            val story = game.getStory(storyId)
            story?.addMessage(message, sessionId)
        }
    }

    fun completeStory(storyId: String, sessionId: String, message: String) {
        val player = getPlayer(sessionId)
        if(player == null || storyNotInPlayerQueue(storyId, player)) {
            return
        }

        lobbyState = game.completeStory(storyId, player, message)
        if(lobbyState == LobbyStateType.READING) {
            addCompletedStoriesToGallery()
        }
    }

    private fun storyNotInPlayerQueue(storyId: String, player: Player): Boolean {
        val playerStories = game.stories[player.username]
        return playerStories == null || playerStories.none { it.id == storyId }
    }

    fun getPlayerBySessionId(sessionId: String): Player? {
        return players.find { it.clientId == sessionId }
    }

    fun endRound(sessionId: String) {
        if(isCreator(sessionId)) {
            game.completeAllStories(sessionId)
            lobbyState = LobbyStateType.READING
            addCompletedStoriesToGallery()
        }
    }

    fun canAddMessageToStory(storyId: String, sessionId: String): Boolean {
        val story = game.getStory(storyId)
        val player = getPlayerBySessionId(sessionId)
        return player != null && story != null && (game.stories[player.username]?.contains(story) != null)

    }

    fun getStoryCreators(): List<Player> {
        return players.filter { player: Player -> game.completedStories.union(game.stories.values.flatten())
            .any{story -> story.creatingPlayer == player } }
    }
}
