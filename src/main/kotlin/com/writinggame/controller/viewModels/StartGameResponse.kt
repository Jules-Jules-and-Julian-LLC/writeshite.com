package com.writinggame.controller.viewModels

import com.writinggame.domain.LobbyStateType
import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Lobby

class StartGameResponse(lobby: Lobby) {
    val responseType: ResponseType = ResponseType.START_GAME
    val lobbyState: LobbyStateType = lobby.lobbyState
    val game: Game = lobby.game
    val gallery = lobby.gallery
}