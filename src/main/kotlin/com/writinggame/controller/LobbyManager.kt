package com.writinggame.controller

import com.writinggame.database.LobbyBag
import com.writinggame.database.PlayerBag
import com.writinggame.database.StoryBag
import com.writinggame.domain.LobbyStateType
import com.writinggame.domain.StoryStateType
import com.writinggame.model.*
import org.apache.ibatis.session.SqlSession
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor

object LobbyManager {
    fun joinLobby(username: String, sessionId: String, lobbyId: String, session: SqlSession): Lobby {
        val player = Player(sessionId, username, lobbyId, session)
        var lobby: Lobby? = LobbyBag(session).lenientFind(lobbyId)
        if(lobby == null) {
            lobby = Lobby(lobbyId, LobbyStateType.GATHERING_PLAYERS, player.username, session = session).save()
        }
        //TODO fix the type hinting
        //Can't save until the lobby exists
        player.save<Player>()

        return lobby
    }

    fun leaveLobby(sessionId: String, session: SqlSession) {
        //Delete player
        //If lobby was owned by player, change lobby owner
        //If player was editing any stories, pass them to someone else
        TODO("Not yet implemented")
    }

    fun startGame(lobbyId: String, sessionId: String, settings: GameSettings, session: SqlSession) {
//        println("Starting game for lobby $lobbyId player $sessionId can start: ${lobby.playerCanStartGame(sessionId)} round time: ${settings.roundTimeMinutes}")
        //If player is creator, change lobby game state to playing and create a game with stories to play
        val lobbyBag = LobbyBag(session)
        val lobby: Lobby = lobbyBag.find(lobbyId)
        val player: Player = PlayerBag(session).findByClientId(sessionId)
        if(player.username == lobby.creatorUsername) {
            val game = Game(-1, settings.roundTimeMinutes,
                if (settings.roundTimeMinutes != null) ZonedDateTime.now().plusMinutes(settings.roundTimeMinutes.toLong()) else null,
                settings.minWordsPerMessage,
                settings.maxWordsPerMessage,
                lobbyId,
                session
            ).save<Game>()
            val playerNames: List<String> = lobbyBag.findPlayerNames(lobbyId)
            playerNames.forEach {
                Story(-1, game.id, it, it, StoryStateType.ACTIVE, session).save<Story>()
            }
        }
    }

    fun addMessageToStory(lobbyId: String, message: String, storyId: Int, sessionId: String, session: SqlSession) {
        println("Adding message for lobby $lobbyId player $sessionId message $message storyId $storyId")
        val story: Story = StoryBag(session).find(storyId)
        val player: Player = PlayerBag(session).findByClientId(sessionId)
        if(story.editingUsername == player.username) {
            Message(-1, storyId, player.username, message, session).save<Message>()
        }
    }

    fun completeStory(lobbyId: String, sessionId: String, storyId: String) {
        println("Completing story: $storyId for lobby: $lobbyId by user: $sessionId")
        //If player is editing story, add story to gallery and set story state to COMPLETE
        //If no stories in game, completeGame
        TODO("Not yet implemented")
    }

    fun completeGame(lobbyId: String) {
        //Move all existing stories from game to gallery
        //Delete game (and children)
    }
}