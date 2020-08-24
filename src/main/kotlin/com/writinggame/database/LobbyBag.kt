package com.writinggame.database

import com.writinggame.controller.viewModels.*
import com.writinggame.model.Lobby
import com.writinggame.model.Story
import org.apache.ibatis.session.SqlSession

class LobbyBag(session: SqlSession): PersistBag(session) {
    fun findCurrentLobbyState(lobbyId: String): LobbyViewModel {
        val lobby = find<Lobby>(lobbyId)
        val game = GameBag(session).findByLobbyId(lobbyId)
        val stories: List<Story> = StoryBag(session).findByGameId(game.id)

        val storyViewModels = stories.map { story ->
            val messages = MessageBag(session).findByStoryId(story.id)
            val messageViewModels = messages.map{ MessageViewModel(it.creatorUsername, it.text) }
            StoryViewModel(story.creatorUsername, story.editingUsername, messageViewModels)
        }
        val gameViewModel = GameViewModel(game, storyViewModels)

        val players = PlayerBag(session).findByLobbyId(lobbyId)
        val playerViewModels = players.map { PlayerViewModel(it.username) }

        return LobbyViewModel(lobby.lobbyId, lobby.state, lobby.creatorUsername, lobby.createDatetime, lobby.lastUpdateDatetime,
            gameViewModel, playerViewModels)
    }

    fun findPlayerNames(lobbyId: String): List<String> {
        return session.selectList("com.writinggame.db.mappers.Lobby.findPlayerNames", lobbyId)
    }
}