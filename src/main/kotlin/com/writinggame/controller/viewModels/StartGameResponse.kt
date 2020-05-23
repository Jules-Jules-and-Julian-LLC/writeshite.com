package com.writinggame.controller.viewModels

import com.writinggame.domain.GameStateType
import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby

class StartGameResponse(lobby: Lobby) {
    val responseType: ResponseType = ResponseType.START_GAME
    val gameState: GameStateType = lobby.gameState
    val game: Game = lobby.game
    val gallery = lobby.gallery
}