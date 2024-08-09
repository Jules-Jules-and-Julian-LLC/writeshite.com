package com.writinggame.controller

import com.writinggame.model.Gallery
import com.writinggame.model.GalleryManager
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@RestController
@CrossOrigin(origins = ["http://localhost:3000", "https://localhost", "https://www.writeshite.com", "https://writeshite.com"])
class GalleryController {
    @GetMapping("/gallery/{lobbyId}/")
    fun getGallery(@PathVariable("lobbyId") lobbyId: String) : String {
        val path = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request.requestURI
        println("GET gallery/${lobbyId}/, page call")
        println("Redirecting gallery $path to index.html")
        return "forward:/index.html"
    }

    @GetMapping("/gallery/{lobbyId}/get")
    fun getGalleryJson(@PathVariable("lobbyId") lobbyId: String) : Gallery {
        println("GET gallery/${lobbyId}/get, raw JSON call")
        return GalleryManager.loadFromFile(lobbyId)
    }
}