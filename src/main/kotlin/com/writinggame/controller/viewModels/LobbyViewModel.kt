package com.writinggame.controller.viewModels

import com.writinggame.domain.LobbyStateType
import java.time.ZonedDateTime

data class LobbyViewModel(val lobbyId: String, val state: LobbyStateType, val creatorUsername: String, val createDatetime: ZonedDateTime,
                          val lastUpdateDatetime: ZonedDateTime, val game: GameViewModel, val players: List<PlayerViewModel>)
