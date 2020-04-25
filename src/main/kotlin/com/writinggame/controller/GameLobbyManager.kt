package com.writinggame.controller

import com.writinggame.model.GameLobby
import com.writinggame.model.Player

object GameLobbyManager {
    private val lobbies: MutableList<GameLobby> = mutableListOf()
    private val idList: List<String> = generateIdList()
    private var idIndex: Int = 0;

    private fun generateIdList(): List<String> {
        val alphabet = "ABCEDFGHIJKLMNOPQRSTUVWXYZ"
        val idList = mutableListOf<String>()
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
        val newLobby = GameLobby(creator, idList[++idIndex])
        lobbies.add(newLobby)
        return newLobby
    }

    fun closeAllEmptyLobbies() {
        lobbies.removeAll { it.players.isEmpty() }
    }
    //TODO cleanup old lobbies
}