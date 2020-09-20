package com.writinggame.model

import java.time.ZonedDateTime

class Gallery(val lobbyId: String, val entries: MutableList<GalleryEntry>) {
    fun addStories(stories: List<Story>) {
        entries.addAll(storiesToEntries(stories))
    }

    private fun storiesToEntries(stories: List<Story>): MutableList<GalleryEntry> {
        val createDatetime = ZonedDateTime.now()

        return stories.filter { it.messages.isNotEmpty() }.map { story ->
            GalleryEntry(story.creatingPlayer.username, story.messages.joinToString(separator = " ") { it.text }, createDatetime)
        }.toMutableList()
    }
}