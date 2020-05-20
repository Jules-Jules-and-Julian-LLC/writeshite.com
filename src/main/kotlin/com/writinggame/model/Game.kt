package com.writinggame.model

import com.writinggame.domain.GameStateType
import kotlin.collections.HashMap

class Game(lobby: Lobby) {
    //Key is username
    val stories: HashMap<String, MutableList<Story>> = initializeStories(lobby)
    val completedStories: MutableList<Story> = mutableListOf()
    private val players = lobby.players

    private fun initializeStories(lobby: Lobby): HashMap<String, MutableList<Story>> {
        val stories = HashMap<String, MutableList<Story>>()
        lobby.players.forEach { stories[it.username] = mutableListOf(Story(it)) }

        return stories
    }

    fun addPlayer(player: Player, gameState: GameStateType) {
        //TODO this allows impersonation but MVP
        if(!stories.containsKey(player.username)) {
            val playerStories = stories.getOrPut(player.username, { mutableListOf() })

            if(gameState == GameStateType.GATHERING_PLAYERS) {
                playerStories.add(Story(player))
            }
        }
    }

    fun passStory(sessionId: String, storyId: String) {
        val player = getPlayer(sessionId)
        val playerQueue = stories[player.username]
        val nextPlayerQueue = getNextPlayerQueue(player)

        val story = getStory(storyId)
        playerQueue!!.remove(story)
        nextPlayerQueue.add(story)
    }

    fun completeStory(storyId: String): GameStateType {
        val story = getStory(storyId)
        completedStories.add(story)

        stories.keys.forEach {username ->
            stories[username]?.removeIf { it.id == storyId }
        }

        return if (stories.values.flatten().isEmpty()) GameStateType.READING else GameStateType.PLAYING
    }

    private fun getPlayer(sessionId: String): Player {
        return players.find { it.clientId == sessionId }!!
    }

    private fun getNextPlayerQueue(player: Player): MutableList<Story> {
        val playersIndex = players.indexOf(player)
        val nextPlayer = players[(playersIndex + 1) % players.size]

        return stories[nextPlayer.username]!!
    }

    fun getStory(storyId: String) : Story {
        //TODO better error message
        return stories.values.flatten().find { it.id == storyId }!!
    }

    fun removePlayer(sessionId: String) {
        val player = getPlayer(sessionId)
        val nextPlayerQueue = getNextPlayerQueue(player)

        while(stories[player.username]!!.isNotEmpty()) {
            nextPlayerQueue.add(stories[player.username]!!.removeAt(0))
        }
    }
}