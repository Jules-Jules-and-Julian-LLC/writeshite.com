package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby

class CompletedStoryResponse(lobby: Lobby) {
    val stories = lobby.game.stories
    val completedStories = lobby.game.completedStories
    val gameState = lobby.gameState
    val responseType = ResponseType.COMPLETED_STORY
}
