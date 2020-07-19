package com.writinggame.model

import com.writinggame.domain.LobbyStateType
import java.time.Instant
import kotlin.collections.HashMap

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
            }
        }
    }

    fun passStory(sessionId: String, storyId: String) {

        val story = getStory(storyId)
        if(story != null) {
            val player = getPlayer(sessionId)
            val playerQueue = stories[player.username]
            val nextPlayerQueue = getNextPlayerQueue(player)

            playerQueue!!.remove(story)
            nextPlayerQueue.add(story)
        }
    }

    fun completeStory(storyId: String): LobbyStateType {
        val story = getStory(storyId)
        if(story != null) {
            completedStories.add(story)

            stories.keys.forEach {username ->
                stories[username]?.removeIf { it.id == storyId }
            }
        }

        return if (stories.values.flatten().isEmpty()) LobbyStateType.READING else LobbyStateType.PLAYING
    }

    private fun getPlayer(sessionId: String): Player {
        return players.find { it.clientId == sessionId }!!
    }

    private fun getNextPlayerQueue(player: Player): MutableList<Story> {
        val playersIndex = players.indexOf(player)
        val nextPlayer = players[(playersIndex + 1) % players.size]

        return stories[nextPlayer.username]!!
    }

    fun getStory(storyId: String) : Story? {
        return stories.values.flatten().find { it.id == storyId }
    }

    fun removePlayer(sessionId: String) {
        val player = getPlayer(sessionId)
        val nextPlayerQueue = getNextPlayerQueue(player)

        while(stories[player.username]!!.isNotEmpty()) {
            nextPlayerQueue.add(stories[player.username]!!.removeAt(0))
        }
    }
}