package com.example.oauth2process.oauth2.userinfo

class NaverOAuth2UserInfo(attributes: Map<String, Any>): OAuth2UserInfo(attributes) {
    override fun getId(): String? {
        val res = attributes["response"] ?: return null
        return if (res is Map<*, *>) res["id"] as String else null
    }

    override fun getNickname(): String? {
        val res = attributes["response"] ?: return null
        return if (res is Map<*, *>) res["nickname"] as String else null
    }

    override fun getImageUrl(): String? {
        val res = attributes["response"] ?: return null
        return if (res is Map<*, *>) res["profile_image"] as String else null
    }
}