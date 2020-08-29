package com.writinggame.controller.viewModels

import com.writinggame.domain.GameStateType
import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby
import java.time.ZonedDateTime

class StartGameResponse(lobby: Lobby, eventReceivedDatetime: ZonedDateTime): Response(ResponseType.START_GAME, eventReceivedDatetime) {
    val gameState: GameStateType = lobby.gameState
    val game: Game = lobby.game
    val gallery = lobby.gallery
}