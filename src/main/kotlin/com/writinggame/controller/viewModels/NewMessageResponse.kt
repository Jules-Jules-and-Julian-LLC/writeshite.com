package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import com.writinggame.model.Game
import com.writinggame.model.Player
import com.writinggame.model.Story
import java.util.*
import kotlin.collections.HashMap

class NewMessageResponse(game: Game) {
    val stories: HashMap<String, MutableList<Story>> = game.stories
    val responseType: ResponseType = ResponseType.NEW_MESSAGE
}