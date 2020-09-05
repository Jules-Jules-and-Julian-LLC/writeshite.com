package com.writinggame.controller.viewModels

import com.writinggame.domain.LobbyStateType
import com.writinggame.model.Game
import com.writinggame.model.Lobby
import java.time.LocalDateTime

class LobbyViewModel(lobby: Lobby) {
    val players: List<String> = lobby.players.map { it.username }
    val createDatetime: LocalDateTime = lobby.createDatetime
    var lobbyState: LobbyStateType = lobby.lobbyState
    val game: Game = lobby.game
    val creator: String = lobby.creator.username
}