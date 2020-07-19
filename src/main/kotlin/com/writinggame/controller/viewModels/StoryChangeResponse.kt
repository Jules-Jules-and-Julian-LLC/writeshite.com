package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Lobby
import com.writinggame.model.Story
import kotlin.collections.HashMap

class StoryChangeResponse(lobby: Lobby) {
    val stories: HashMap<String, MutableList<Story>> = lobby.game.stories
    val completedStories = lobby.game.completedStories
    val gameState = lobby.lobbyState
    val responseType: ResponseType = ResponseType.STORY_CHANGE
}