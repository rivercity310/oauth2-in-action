package com.example.oauth2process.oauth2.service

import com.example.oauth2process.oauth2.OAuthAttributes
import com.example.oauth2process.oauth2.userinfo.CustomOAuth2User
import com.example.oauth2process.user.SocialType
import com.example.oauth2process.user.User
import com.example.oauth2process.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class CustomOAuth2UserService(private val userRepository: UserRepository): OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    companion object {
        private const val NAVER = "naver"
        private const val KAKAO = "kakao"
        private const val GOOGLE = "google"
        private val log = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)
    }

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User? {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입")

        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val registrationId = userRequest?.clientRegistration?.registrationId
        val socialType = when (registrationId) {
            NAVER -> SocialType.NAVER
            KAKAO -> SocialType.KAKAO
            GOOGLE -> SocialType.GOOGLE
            else -> return null
        }

        val userNameAttributeName = userRequest.clientRegistration?.providerDetails?.userInfoEndpoint?.userNameAttributeName ?: return null
        val attributes = oAuth2User.attributes

        val extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes)
        val createdUser = getUser(extractAttributes, socialType)

        return CustomOAuth2User(
                userAuthorities = Collections.singleton(SimpleGrantedAuthority(createdUser.role!!.key)),
                userAttributes = attributes,
                nameAttributeKey = extractAttributes.nameAttributeKey,
                email = createdUser.email!!,
                role = createdUser.role!!
        )
    }

    private fun getUser(attributes: OAuthAttributes, socialType: SocialType): User {
        return userRepository.findBySocialTypeAndSocialId(socialType, attributes.oauth2UserInfo.getId()!!) ?: saveUser(attributes, socialType)
    }

    private fun saveUser(attributes: OAuthAttributes, socialType: SocialType): User {
        val createdUser = attributes.toEntity(socialType, attributes.oauth2UserInfo)
        return userRepository.save(createdUser)
    }
}