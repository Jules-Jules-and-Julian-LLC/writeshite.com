package com.writinggame.controller

import com.writinggame.model.Gallery
import com.writinggame.model.GalleryManager
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3000", "https://localhost", "https://www.writeshite.com", "https://writeshite.com"])
class GalleryController {

    @GetMapping("/gallery/{lobbyId}/get")
    fun getGalleryJson(@PathVariable("lobbyId") lobbyId: String) : Gallery {
        return GalleryManager.loadFromFile(lobbyId)
    }
}