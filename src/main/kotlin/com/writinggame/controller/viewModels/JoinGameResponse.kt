package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Lobby

class JoinGameResponse(lobby: Lobby, sessionId: String) {
    val lobby: LobbyViewModel = LobbyViewModel(lobby)
    val creator: Boolean = lobby.isCreator(sessionId)
    val responseType: ResponseType = ResponseType.JOIN_GAME
}