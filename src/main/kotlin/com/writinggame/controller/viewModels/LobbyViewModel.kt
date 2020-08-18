package com.writinggame.controller.viewModels

import com.writinggame.domain.LobbyStateType
import java.time.OffsetDateTime

data class LobbyViewModel(val lobbyId: String, val state: LobbyStateType, val creatorUsername: String, val createDatetime: OffsetDateTime,
                          val lastUpdateDatetime: OffsetDateTime, val game: GameViewModel, val players: List<PlayerViewModel>)
