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

    fun createLobby(creator: Player): GameLobby {
        val id = idList[++idIndex]
        val newLobby = GameLobby(creator, id)
        lobbies[id] = newLobby

        return newLobby
    }

    fun closeAllEmptyLobbies() {
        lobbies.entries.removeIf{ it.value.players.isEmpty() }
    }

    fun closeLobbiesOlderThan(date: ZonedDateTime) {
        throw NotImplementedError()
    }

    fun joinLobby(joiner: Player, lobbyId: String): GameLobby {
        //TODO technical exception
        val lobby = getLobby(lobbyId)
        lobby.addPlayer(joiner)

        return lobby
    }

    fun getLobby(lobbyId: String): GameLobby {
        return lobbies[lobbyId] ?: throw Exception("missing lobby")
    }

    fun startGame(lobbyId: String) {
        getLobby(lobbyId).startGame()
    }
}