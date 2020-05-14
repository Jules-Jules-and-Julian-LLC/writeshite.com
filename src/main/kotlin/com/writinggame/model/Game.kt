package com.writinggame.model

class Game(lobby: Lobby) {
    //Maps from username to that user's current story queue
    val liveStories: LinkedHashMap<String, MutableList<Story>> = initializeStories(lobby)
    val finishedStories: MutableList<Story> = mutableListOf()

    private fun initializeStories(lobby: Lobby): LinkedHashMap<String, MutableList<Story>> {
        val stories: LinkedHashMap<String, MutableList<Story>> = linkedMapOf()
        lobby.players.values.forEach {
            stories[it.username] = mutableListOf(Story(it.clientId))
        }

        return stories
    }

    fun addPlayer(player: Player) {
        if(!liveStories.containsKey(player.username)) {
            liveStories[player.username] = mutableListOf(Story(player.clientId))
        }
    }

    fun passStory(storyId: String, passingUsername: String) {
        //TODO this sucks but MVP
        val story = getStory(storyId)
        val nextUserStories = getNextUserStories(passingUsername)

        removeStory(passingUsername, story)
        nextUserStories.add(story)
    }

    private fun removeStory(passingUsername: String, story: Story) {
        liveStories[passingUsername]?.remove(story)
    }

    private fun getNextUserStories(passingUsername: String): MutableList<Story> {
        var found = false;
        var foundStories: MutableList<Story>? = null
        liveStories.entries.forEach {
            if(found) {
                foundStories = it.value
            }
            found = it.key == passingUsername
        }

        return foundStories ?: liveStories.values.first()
    }

    fun getStory(storyId: String) : Story {
        return liveStories.values.flatten().find { it.storyId == storyId }!!
    }
}