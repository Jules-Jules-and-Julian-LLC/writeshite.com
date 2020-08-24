package com.writinggame.controller.viewModels

import com.writinggame.model.Game
import com.writinggame.model.Story
import java.time.OffsetDateTime

data class GameViewModel(val roundTimeMinutes: Int?, val roundEndDatetime: OffsetDateTime?, val minWordsPerMessage: Int?,
    val maxWordsPerMessage: Int?, val stories: List<StoryViewModel>) {
    constructor(game: Game, stories: List<StoryViewModel>) : this(game.roundTimeMinutes, game.roundEndDatetime, game.minWordsPerMessage,
        game.maxWordsPerMessage, stories)
}
