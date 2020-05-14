package com.writinggame.controller.viewModels

import com.writinggame.domain.GameStateType
import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby
import com.writinggame.model.Story

class NewMessageResponse(val liveStories: LinkedHashMap<String, MutableList<Story>>) {
    val responseType: ResponseType = ResponseType.NEW_MESSAGE
}