package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Story

class NewMessageResponse(game: Game) {
    val stories: MutableList<Story> = game.stories
    val responseType: ResponseType = ResponseType.NEW_MESSAGE
}