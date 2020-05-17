package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Story

class NewMessageResponse(gameViewModel: GameViewModel) {
    val stories: LinkedHashMap<String, List<Story>> = gameViewModel.stories
    val responseType: ResponseType = ResponseType.NEW_MESSAGE
}