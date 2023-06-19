package com.example.oauth2process.oauth2.userinfo

class KakaoOAuth2UserInfo(attributes: Map<String, Any>): OAuth2UserInfo(attributes) {
    override fun getId(): String? {
        return attributes["id"] as String?
    }

    override fun getNickname(): String? {
        val account = attributes["kakao_account"] as Map<*, *>?
        val profile = account?.get("profile") as Map<*, *>?
        return if (account == null || profile == null) null else profile["nickname"] as String
    }

    override fun getImageUrl(): String? {
        val account = attributes["kakao_account"] as Map<*, *>?
        val profile = account?.get("profile") as Map<*, *>?
        return if (account == null || profile == null) null else profile["thumbnail_image_url"] as String
    }
}