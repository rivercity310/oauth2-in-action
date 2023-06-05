package com.example.demo.controller

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.util.logging.Logger
import kotlin.reflect.jvm.jvmName

@Controller
class MainController {
    private val logger: Logger = Logger.getLogger(MainController::class.jvmName)

    @GetMapping("/")
    internal fun main(token: OAuth2AuthenticationToken): String {
        logger.info(token.principal.toString())
        return "main.html"
    }
}