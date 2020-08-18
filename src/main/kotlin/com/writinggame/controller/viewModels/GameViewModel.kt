package com.writinggame.controller.viewModels

import java.time.OffsetDateTime

data class GameViewModel(val roundTimeMinutes: Int, val roundEndDatetime: OffsetDateTime, val minWordsPerMessage: Int,
    val maxWordsPerMessage: Int, val stories: List<StoryViewModel>)
