package com.writinggame.model

import com.writinggame.domain.StoryPassStyleType

class GameSettings(val roundTimeMinutes: Long? = 10, val minWordsPerMessage: Int? = null, val maxWordsPerMessage: Int? = null,
    val passStyle: StoryPassStyleType = StoryPassStyleType.MINIMIZE_WAIT
)