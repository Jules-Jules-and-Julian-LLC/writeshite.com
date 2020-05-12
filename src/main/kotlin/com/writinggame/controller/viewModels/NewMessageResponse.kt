package com.writinggame.controller.viewModels

import com.writinggame.domain.GameStateType
import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby

class NewMessageResponse(val message: String, val storyId: String, val username: String) {
    val responseType: ResponseType = ResponseType.NEW_MESSAGE
}