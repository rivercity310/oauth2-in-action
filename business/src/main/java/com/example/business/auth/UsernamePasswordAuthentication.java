package com.example.business.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/* 사용자 이름과 암호를 이용한 인증을 구현하는 클래스 */
public class UsernamePasswordAuthentication extends UsernamePasswordAuthenticationToken {
    /* 인증 인스턴스가 인증되지 않은 상태로 유지 -> AuthenticationManager가 요청을 인증할 올바른 인증공급자 객체를 찾는다 */
    public UsernamePasswordAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    /* Authentication 객체가 인증됨 -> 인증 프로세스 완료 */
    public UsernamePasswordAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
