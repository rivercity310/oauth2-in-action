package com.example.oauth2.business;

import com.example.oauth2.domain.Member;
import com.example.oauth2.domain.Otp;
import com.example.oauth2.repository.MemberRepository;
import com.example.oauth2.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MemberService {
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MemberRepository memberRepository;
    @Autowired private OtpRepository otpRepository;

    // 사용자를 추가한다
    public void addMember(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    // otp 검증
    public boolean check(Otp otpToValidate) {
        Optional<Otp> userOtp = otpRepository.findOtpByUsername(otpToValidate.getUsername());

        if (userOtp.isPresent()) {
            Otp otp = userOtp.get();
            return otpToValidate.getCode().equals(otp.getCode());
        }

        return false;
    }

    // 사용자를 검증하고 일치하면 otp 생성, (sms로 사용자에게 생성된 otp 전송)
    public void auth(Member member) {
        Optional<Member> o = memberRepository.findMemberByUsername(member.getUsername());

        // 사용자가 없는 경우 예외
        if (o.isEmpty()) throw new BadCredentialsException("Bad credentials");

        Member m = o.get();

        // 암호가 일치하지 않으면 예외
        if (!passwordEncoder.matches(member.getPassword(), m.getPassword()))
            throw new BadCredentialsException("Bad credentials");

        // 모든 예외를 통과했으면 otp 발행
        renewOtp(m);
    }

    private void renewOtp(Member m) {
        // otp를 위한 난수 생성
        String code = GenerateCodeUtil.generateCode();

        Optional<Otp> userOtp = otpRepository.findOtpByUsername(m.getUsername());

        // 사용자 이름에 대한 otp가 이미 있으면 값 업데이트, 없으면 새 레코드 생성
        if (userOtp.isPresent()) {
            Otp otp = userOtp.get();
            otp.setCode(code);
        } else {
            Otp otp = new Otp();
            otp.setUsername(m.getUsername());
            otp.setCode(code);
            otpRepository.save(otp);
        }
    }
}
