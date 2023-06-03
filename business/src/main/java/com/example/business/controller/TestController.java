package com.example.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    // 보호하려는 테스트 엔드포인트
    @GetMapping("/test")
    public String test() {
        return "Test";
    }
}