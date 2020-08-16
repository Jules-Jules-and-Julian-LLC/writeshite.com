package com.writinggame.controller.viewModels

import java.time.ZonedDateTime

data class GameViewModel(val roundTimeMinutes: Int, val roundEndDatetime: ZonedDateTime, val minWordsPerMessage: Int,
    val maxWordsPerMessage: Int, val stories: List<StoryViewModel>)
