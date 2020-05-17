package com.writinggame.model

class Game(lobby: Lobby) {
    val stories: MutableList<Story> = initializeStories(lobby)
    private val players = lobby.players

    private fun initializeStories(lobby: Lobby): MutableList<Story> {
        return lobby.players.map(::Story).toMutableList()
    }

    fun addPlayer(player: Player) {
        if(!stories.any { story -> story.creatingPlayer == player}) {
            stories.add(Story(player))
        }
    }

    fun passStory(storyId: String) {
        val story = getStory(storyId)

        story.editingPlayer = getNextPlayer(story.editingPlayer)

        //Move to end of the list, so it appears in players queues correctly
        stories.remove(story)
        stories.add(story)
    }

    private fun getNextPlayer(editingPlayer: Player): Player {
        val playersIndex = players.indexOf(editingPlayer)

        return players[(playersIndex + 1) % players.size]
    }

    fun getStory(storyId: String) : Story {
        //TODO better error message
        return stories.find { it.id == storyId }!!
    }
}