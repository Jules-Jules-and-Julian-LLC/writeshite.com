package com.writinggame.model

class Game(lobby: Lobby) {
    val liveStories: MutableList<Story> = initializeStories(lobby)
    val finishedStories: MutableList<Story> = mutableListOf()

    private fun initializeStories(lobby: Lobby): MutableList<Story> {
        return lobby.players.values.map { Story(it.clientId) }.toMutableList()
    }
}