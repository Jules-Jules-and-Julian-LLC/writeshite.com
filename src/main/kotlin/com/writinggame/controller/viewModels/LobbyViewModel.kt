package com.writinggame.controller.viewModels

import com.writinggame.domain.LobbyStateType
import java.time.ZonedDateTime

data class LobbyViewModel(val lobbyId: String, val lobbyState: LobbyStateType, val createDateTime: ZonedDateTime,
val lastUpdateDateTime: ZonedDateTime, val game: GameViewModel)
