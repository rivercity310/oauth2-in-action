package com.example.oauth2process.oauth2.userinfo

class GoogleOAuth2UserInfo(attributes: Map<String, Any>): OAuth2UserInfo(attributes) {
    override fun getId(): String? {
        return attributes["sub"] as String?
    }

    override fun getNickname(): String? {
        return attributes["name"] as String?
    }

    override fun getImageUrl(): String? {
        return attributes["picture"] as String?
    }
}