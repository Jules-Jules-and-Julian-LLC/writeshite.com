package com.writinggame.model

import java.lang.Exception
import java.time.ZonedDateTime

object GameLobbyManager {
    private val lobbies: HashMap<String, GameLobby> = HashMap()
    private val idList: List<String> = generateIdList()
    private var idIndex: Int = 0;

    private fun generateIdList(): List<String> {
        val alphabet = "ABCEDFGHIJKLMNOPQRSTUVWXYZ"
        val idList = mutableListOf<String>()
        //TODO this feels stupid. There should be some smarter algorithm but also whatever
        for(i in alphabet) {
            for(j in alphabet) {
                for(k in alphabet) {
                    for (l in alphabet) {
                        idList.add("$i$j$k$l")
                    }
                }
            }
        }

        return idList.shuffled()
    }

    fun createLobby(givenId: String? = null): GameLobby {
        val id = givenId ?: idList[++idIndex]
        val newLobby = GameLobby(id)
        lobbies[id] = newLobby

        return newLobby
    }

    fun closeAllEmptyLobbies() {
        lobbies.entries.removeIf{ it.value.players.isEmpty() }
    }

    fun closeLobbiesOlderThan(date: ZonedDateTime) {
        throw NotImplementedError()
    }

    fun joinLobby(username: String, lobbyId: String): GameLobby {
        //TODO technical exception
        //TODO this lets people create arbitrary lobbies, which is good and bad
        if(!lobbyExists(lobbyId)) {
            createLobby(lobbyId)
        }
        val lobby = getLobby(lobbyId)
        lobby.addPlayer(Player("asdfasdf", username))

        return lobby
    }

    private fun lobbyExists(lobbyId: String): Boolean {
        return lobbies[lobbyId] != null
    }

    fun getLobby(lobbyId: String): GameLobby {
        return lobbies[lobbyId] ?: throw Exception("missing lobby")
    }

    fun startGame(lobbyId: String) {
        getLobby(lobbyId).startGame()
    }
}