package com.vividh.url_shortner.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class IdGeneratorService {

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    public String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            int idx = random.nextInt(62);
            shortCode.append(BASE62.charAt(idx));
        }
        return shortCode.toString();
    }

}
