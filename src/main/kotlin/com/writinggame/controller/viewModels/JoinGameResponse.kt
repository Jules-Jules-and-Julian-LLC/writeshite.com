package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Lobby

class JoinGameResponse(val lobby: Lobby, sessionId: String) {
    val responseType: ResponseType = ResponseType.JOIN_GAME
}