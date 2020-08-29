package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby
import com.writinggame.model.Player
import com.writinggame.model.Story
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.HashMap

class StoryChangeResponse(lobby: Lobby, eventReceivedDatetime: ZonedDateTime): Response(ResponseType.STORY_CHANGE, eventReceivedDatetime) {
    val stories: HashMap<String, MutableList<Story>> = lobby.game.stories
    val completedStories = lobby.game.completedStories
    val gameState = lobby.gameState
}