package com.writinggame.controller.viewModels

import com.writinggame.model.Game
import com.writinggame.model.Story

class GameViewModel(game: Game) {
    val stories: LinkedHashMap<String, List<Story>> = initializeStories(game)

    private fun initializeStories(game: Game): LinkedHashMap<String, List<Story>> {
        val toRet: LinkedHashMap<String, List<Story>> = linkedMapOf()
        game.players.forEach {player ->
            val playerStories = game.stories.filter { story -> story.creatingPlayer == player.username }
            toRet[player.username] = playerStories
        }

        return toRet
    }
}