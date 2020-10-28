package com.writinggame.model

import com.writinggame.domain.StoryPassStyleType

class GameSettings(val roundTimeMinutes: Long? = null, val minWordsPerMessage: Int? = null, val maxWordsPerMessage: Int? = null,
    val passStyle: StoryPassStyleType = StoryPassStyleType.ORDERED, val saveStoriesToGallery: Boolean = true)