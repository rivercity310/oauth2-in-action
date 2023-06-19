package com.example.oauth2process.oauth2.userinfo

abstract class OAuth2UserInfo(protected val attributes: Map<String, Any>) {
    abstract fun getId(): String?
    abstract fun getNickname(): String?
    abstract fun getImageUrl(): String?
}