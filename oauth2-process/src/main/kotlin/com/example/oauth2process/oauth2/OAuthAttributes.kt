package com.example.oauth2process.oauth2

import com.example.oauth2process.oauth2.userinfo.GoogleOAuth2UserInfo
import com.example.oauth2process.oauth2.userinfo.KakaoOAuth2UserInfo
import com.example.oauth2process.oauth2.userinfo.NaverOAuth2UserInfo
import com.example.oauth2process.oauth2.userinfo.OAuth2UserInfo
import com.example.oauth2process.user.Role
import com.example.oauth2process.user.SocialType
import com.example.oauth2process.user.User
import java.util.UUID

class OAuthAttributes(
    val nameAttributeKey: String,
    val oauth2UserInfo: OAuth2UserInfo
) {
    companion object {
        fun of(socialType: SocialType, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return when (socialType) {
                SocialType.NAVER -> ofNaver(userNameAttributeName, attributes)
                SocialType.KAKAO -> ofKakao(userNameAttributeName, attributes)
                SocialType.GOOGLE -> ofGoogle(userNameAttributeName, attributes)
            }
        }

        private fun ofKakao(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                    nameAttributeKey = userNameAttributeName,
                    oauth2UserInfo = KakaoOAuth2UserInfo(attributes)
            )
        }

        private fun ofNaver(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                    nameAttributeKey = userNameAttributeName,
                    oauth2UserInfo = NaverOAuth2UserInfo(attributes)
            )
        }

        private fun ofGoogle(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                    nameAttributeKey = userNameAttributeName,
                    oauth2UserInfo = GoogleOAuth2UserInfo(attributes)
            )
        }
    }

    fun toEntity(socialType: SocialType, oauth2UserInfo: OAuth2UserInfo): User {
        return User(
                socialType = socialType,
                socialId = oauth2UserInfo.getId(),
                email = "${UUID.randomUUID()}@socialUser.com",
                nickname = oauth2UserInfo.getNickname(),
                imageUrl = oauth2UserInfo.getImageUrl(),
                role = Role.GUEST
        )
    }
}