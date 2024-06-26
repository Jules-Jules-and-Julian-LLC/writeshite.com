package com.writinggame.model

import com.writinggame.domain.LobbyStateType
import com.writinggame.domain.StoryPassStyleType
import java.time.Instant
import java.time.ZonedDateTime

class Game(lobby: Lobby, val settings: GameSettings) {
    //Key is username
    val stories: HashMap<String, MutableList<Story>> = initializeStories(lobby)
    val completedStories: MutableList<Story> = mutableListOf()
    private val players = lobby.players
    val endTime: Instant? = if (settings.roundTimeMinutes != null) Instant.now().plusSeconds(60 * settings.roundTimeMinutes) else null

    private fun initializeStories(lobby: Lobby): HashMap<String, MutableList<Story>> {
        val stories = HashMap<String, MutableList<Story>>()
        lobby.players.forEach { stories[it.username] = mutableListOf(Story(it)) }

        return stories
    }

    fun addPlayer(player: Player, lobbyState: LobbyStateType) {
        if(!stories.containsKey(player.username)) {
            val playerStories = stories.getOrPut(player.username, { mutableListOf() })

            if(lobbyState == LobbyStateType.GATHERING_PLAYERS) {
                playerStories.add(Story(player))
            } else {
                player.waitingSince = ZonedDateTime.now()
            }
        }
    }

    fun passStory(sessionId: String, storyId: String) {
        val story = getStory(storyId)
        if(story != null) {
            val player = getPlayer(sessionId)
            val playerQueue = stories[player.username]
            val nextPlayer = getPlayerToPassTo(player)
            val nextPlayerQueue = stories[nextPlayer?.username]

            playerQueue?.remove(story)
            nextPlayerQueue?.add(story)

            if(playerQueue?.isEmpty() == true) {
                player.waitingSince = ZonedDateTime.now()
            }
            nextPlayer?.waitingSince = null
        }
    }

    private fun completeStory(story: Story?, completingPlayer: Player, message: String): LobbyStateType {
        if(story != null) {
            story.addMessage(message, completingPlayer.clientId)
            completedStories.add(story)

            stories.keys.forEach {username ->
                stories[username]?.removeIf { it.id == story.id }
            }
        }

        if(stories[completingPlayer.username]?.isEmpty() == true) {
            completingPlayer.waitingSince = ZonedDateTime.now()
        }

        return if (stories.values.flatten().isEmpty()) LobbyStateType.READING else LobbyStateType.PLAYING
    }

    fun completeStory(storyId: String, completingPlayer: Player, message: String): LobbyStateType {
        val story = getStory(storyId)
        return completeStory(story, completingPlayer, message)
    }

    //TODO remove !!
    private fun getPlayer(sessionId: String): Player {
        return players.find { it.clientId == sessionId }!!
    }

    //TODO remove !!
    private fun getPlayerByUsername(username: String): Player {
        return players.find { it.username == username }!!
    }

    private fun getPlayerToPassTo(player: Player): Player? {
        if(players.size <= 1) {
            return if (players.size == 1) player else null
        }
        return when(settings.passStyle) {
            StoryPassStyleType.ORDERED -> getNextPlayer(player)
            StoryPassStyleType.RANDOM -> getRandomPlayer(player)
            StoryPassStyleType.MINIMIZE_WAIT -> getMinimizeWaitPlayer(player)
        }
    }

    private fun getNextPlayer(player: Player): Player {
        val playersIndex = players.indexOf(player)
        return players[(playersIndex + 1) % players.size]
    }

    private fun getRandomPlayer(player: Player): Player {
        return players.filterNot { it.username == player.username }.random()
    }

    private fun getMinimizeWaitPlayer(player: Player): Player {
        val notMyStories = stories.filter { it.key != player.username }
        val minStories = notMyStories.map { it.value.size }.minOrNull()
        val minStoryQueues = notMyStories.filter { it.value.size == minStories }

        if(minStoryQueues.keys.size == 1) {
            return getPlayerByUsername(minStoryQueues.keys.first())
        }

        val waitingPlayers = notMyStories.filter { getPlayerByUsername(it.key).waitingSince != null }
        return if(waitingPlayers.isEmpty()) {
            getRandomPlayer(player)
        } else {
            getPlayerByUsername(waitingPlayers.minByOrNull { getPlayerByUsername(it.key).waitingSince!! }!!.key)
        }
    }

    fun getStory(storyId: String) : Story? {
        return stories.values.flatten().find { it.id == storyId }
    }

    fun removePlayer(sessionId: String) {
        val player = getPlayer(sessionId)
        val nextPlayer = getPlayerToPassTo(player)
        val nextPlayerQueue = stories[nextPlayer?.username]

        nextPlayerQueue?.addAll(stories[player.username] ?: mutableListOf())
        stories.remove(player.username)
    }

    fun completeAllStories(sessionId: String) {
        while(stories.values.flatten().isNotEmpty()) {
            completeStory(stories.values.flatten().elementAt(0), getPlayer(sessionId), "")
        }
    }
}