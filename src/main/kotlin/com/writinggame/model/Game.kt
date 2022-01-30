package com.writinggame.model

import java.util.Collections.shuffle

class Game(lobby: Lobby, val settings: GameSettings) {
    val playerHands: MutableMap<String, MutableList<Int>> = dealHands(lobby.startingLevel)
    var dealtCards: MutableList<Int> = mutableListOf()
    var lives: Int = lobby.players.size
    var stars: Int = 1
    var level: Int = lobby.startingLevel
    val playedCards: MutableList<Int> = mutableListOf()
    private val players = lobby.players

    private fun dealHands(level: Int): MutableMap<String, MutableList<Int>> {
        val hands: MutableMap<String, MutableList<Int>> = mutableMapOf()
        val deck: MutableList<Int> = createDeck()
        dealtCards = mutableListOf()

        players.forEach { player ->
            (0..level).forEach { _ ->
                //TODO May be O(N) to remove from the list, may be worth investigating but probably fine for this scale
                val cardToDeal = deck.removeAt(0)
                hands.getOrDefault(player.username, mutableListOf()).add(cardToDeal)
                dealtCards.add(cardToDeal)
            }
            //?. should never fail, the above loop will initialize for this player
            hands[player.username]?.sort()
        }

        dealtCards.sort()

        return hands
    }

    private fun createDeck(size: Int = 100): MutableList<Int> {
        val deck = (0..size).toMutableList()
        shuffle(deck)
        return deck
    }

    fun playCard(username: String): String? {
        val playerHand = playerHands[username]
        if (playerHand != null) {
            val cardPlayed = playerHand.removeAt(0)
            if(cardPlayed != dealtCards[0]) {
                //TODO this is not correct, it should instead lose a life and add all skipped numbers then refocus
                return "Cards played out of order, YOU LOSE!"
            }
            playedCards.add(cardPlayed)
            dealtCards.removeAt(0)

            return null
        }

        return null
    }

    //TODO remove !!
    private fun getPlayer(sessionId: String): Player {
        return players.find { it.clientId == sessionId }!!
    }

    //TODO remove !!
    private fun getPlayerByUsername(username: String): Player {
        return players.find { it.username == username }!!
    }
}