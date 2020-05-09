package com.writinggame.controller.viewModels

import com.writinggame.domain.GameStateType
import com.writinggame.domain.ResponseType

class StartGameResponse(val gameState: GameStateType) {
    val responseType: ResponseType = ResponseType.START_GAME
}