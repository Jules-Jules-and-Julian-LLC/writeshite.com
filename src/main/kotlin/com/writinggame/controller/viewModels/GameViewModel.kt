package com.writinggame.controller.viewModels

import com.writinggame.model.Game
import com.writinggame.model.Story

//TODO remove if not needed for a while
class GameViewModel(game: Game) {
    val liveStories: MutableList<Story> = game.liveStories
    val finishedStories: MutableList<Story> = game.finishedStories
}