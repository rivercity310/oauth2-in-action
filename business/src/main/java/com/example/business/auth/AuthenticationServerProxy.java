package com.example.business.auth;

import com.example.business.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationServerProxy {
    @Autowired
    private RestTemplate rest;

    @Value("${auth.server.base.url}")
    private String baseUrl;

    public void sendAuth(String username, String password) {
        String url = baseUrl + "/user/auth";

        Member body = new Member();
        body.setUsername(username);
        body.setPassword(password);

        HttpEntity<Member> request = new HttpEntity<>(body);

        rest.postForEntity(url, request, Void.class);
    }

    public boolean sendOtp(String username, String code) {
        String url = baseUrl + "/otp/check";

        Member body = new Member();
        body.setUsername(username);
        body.setCode(code);

        HttpEntity<Member> request = new HttpEntity<>(body);
        ResponseEntity<Void> response = rest.postForEntity(url, request, Void.class);

        return response.getStatusCode().equals(HttpStatus.OK);
    }
}
