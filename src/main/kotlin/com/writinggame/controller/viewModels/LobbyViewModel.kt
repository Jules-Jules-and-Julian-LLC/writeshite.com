package com.writinggame.controller.viewModels

import com.writinggame.domain.GameStateType
import com.writinggame.model.Lobby
import java.time.LocalDateTime

class LobbyViewModel(lobby: Lobby) {
    val players: List<String> = lobby.players.values.map { it.username }
    val createDatetime: LocalDateTime = lobby.createDatetime
    var gameState: GameStateType = lobby.gameState
}