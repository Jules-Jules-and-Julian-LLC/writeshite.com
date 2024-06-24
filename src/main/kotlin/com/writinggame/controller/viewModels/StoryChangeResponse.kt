package com.writinggame.controller.viewModels

import com.writinggame.domain.LobbyStateType
import com.writinggame.domain.ResponseType
import com.writinggame.model.Lobby
import com.writinggame.model.Story
import java.time.ZonedDateTime
import kotlin.collections.HashMap

class StoryChangeResponse(lobby: Lobby, eventReceivedDatetime: ZonedDateTime): Response(ResponseType.STORY_CHANGE, eventReceivedDatetime) {
    val stories: HashMap<String, MutableList<Story>> = lobby.game.stories
    val completedStories = lobby.game.completedStories
    val lobbyState = lobby.lobbyState
    val players = lobby.players
    val readingOrder = if (lobby.lobbyState == LobbyStateType.READING) lobby.getStoryCreators().shuffled() else null
}