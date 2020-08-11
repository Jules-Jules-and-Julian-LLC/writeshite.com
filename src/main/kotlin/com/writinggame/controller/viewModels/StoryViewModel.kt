package com.writinggame.controller.viewModels

data class StoryViewModel(val creatorUsername: String, val editingUsername: String, val messages: List<MessageViewModel>)