package com.example.Angler.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    //DI 등
    @GetMapping
    public ResponseEntity<String> securityTest(){
        return ResponseEntity
                .ok("Hi!");
    }

}
