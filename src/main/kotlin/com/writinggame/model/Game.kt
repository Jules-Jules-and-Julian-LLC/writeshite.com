package com.writinggame.model

class Game(lobby: Lobby) {
    //Maps from username to that user's current story queue
    val liveStories: HashMap<String, MutableList<Story>> = initializeStories(lobby)
    val finishedStories: MutableList<Story> = mutableListOf()

    private fun initializeStories(lobby: Lobby): HashMap<String, MutableList<Story>> {
        val stories: HashMap<String, MutableList<Story>> = hashMapOf()
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
}