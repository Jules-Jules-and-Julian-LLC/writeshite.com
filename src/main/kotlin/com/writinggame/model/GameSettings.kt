package com.writinggame.model

import com.writinggame.domain.StoryPassStyleType

data class GameSetting<T>(var value: T, val name: String, val description: String)


class GameSettings(val roundTimeMinutes: Long? = null, val minWordsPerMessage: Int? = null, val maxWordsPerMessage: Int? = null,
    val passStyle: StoryPassStyleType = StoryPassStyleType.ORDERED, val saveStoriesToGallery: Boolean = true,
    val exquisiteCorpse: Boolean = false)