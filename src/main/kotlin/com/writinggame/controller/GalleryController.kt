package com.writinggame.controller

import com.writinggame.model.Gallery
import com.writinggame.model.GalleryManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:3000", "https://localhost", "https://www.writeshite.com", "https://writeshite.com"])
class GalleryController {
    @RequestMapping(value = ["/rawJson/{lobbyId}"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getGalleryJson(@PathVariable("lobbyId") lobbyId: String) : Gallery {
        println("GET rawJson/${lobbyId}, raw JSON call")
        return GalleryManager.loadFromFile(lobbyId)
    }
}