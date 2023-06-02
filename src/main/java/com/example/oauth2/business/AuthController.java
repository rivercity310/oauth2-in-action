package com.example.oauth2.business;

import com.example.oauth2.domain.Member;
import com.example.oauth2.domain.Otp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired private MemberService memberService;

    @PostMapping("/member/add")
    public void addMember(@RequestBody Member member) {
        memberService.addMember(member);
    }

    @PostMapping("/member/auth")
    public void auth(@RequestBody Member member) {
        memberService.auth(member);
    }

    @PostMapping("/otp/check")
    public void check(@RequestBody Otp otp, HttpServletResponse response) {
        if (memberService.check(otp)) response.setStatus(HttpServletResponse.SC_OK);
        else response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
