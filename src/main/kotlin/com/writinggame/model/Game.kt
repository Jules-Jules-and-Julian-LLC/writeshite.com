package com.writinggame.model

class Game(lobby: Lobby) {
    val stories: MutableList<Story> = initializeStories(lobby)
    val players = lobby.players

    private fun initializeStories(lobby: Lobby): MutableList<Story> {
        return lobby.players.map{ Story(it.username) }.toMutableList()
    }

    fun addPlayer(player: Player) {
        if(!stories.any { story -> story.creatingPlayer == player.username}) {
            stories.add(Story(player.username))
        }
    }

    fun passStory(storyId: String) {
        val story = getStory(storyId)

        story.editingPlayer = getNextStory(storyId).creatingPlayer
    }

    private fun getNextStory(storyId: String): Story {
        val storyIndex = stories.indexOfFirst { it.id == storyId }

        return stories[(storyIndex + 1) % stories.size]
    }

    fun getStory(storyId: String) : Story {
        //TODO better error message
        return stories.find { it.id == storyId }!!
    }

    fun removePlayer(sessionId: String) {
        players.removeIf { it.clientId == sessionId }
    }
}