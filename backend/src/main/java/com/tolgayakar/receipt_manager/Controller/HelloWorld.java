package com.tolgayakar.receipt_manager.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class HelloWorld {
    @GetMapping("/test")
    public String getHelloWorld(@RequestParam(required = false) String param) {
        return "Hello World!";
    }

    @GetMapping("/test/auth")
    public String getHelloWorldWithAuthorizedUSer(@RequestParam(required = false) String param) {
        return "Hello world with authorized user";
    }
}
