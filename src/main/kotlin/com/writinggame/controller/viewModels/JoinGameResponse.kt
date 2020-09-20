package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Lobby
import java.time.ZonedDateTime

class JoinGameResponse(val lobby: Lobby, eventReceivedDatetime: ZonedDateTime): Response(ResponseType.JOIN_GAME, eventReceivedDatetime)