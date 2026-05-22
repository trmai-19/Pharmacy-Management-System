package com.pharmacy.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String hash = encoder.encode("Test@1234");

        System.out.println(hash);

        System.out.println(
                encoder.matches("Test@1234", hash)
        );
    }
}