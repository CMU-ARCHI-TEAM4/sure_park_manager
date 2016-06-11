package com.lge.sureparkmanager.manager;

import java.util.Random;

public class TokenGenerator {
    public static String generateToken() {
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            Random random = new Random();
            int randomInt = random.nextInt(10);

            if (randomInt % 2 == 0) {
                int r = random.nextInt(26) + 65;
                token.append((char)r);
            } else {
                int r = random.nextInt(9);
                token.append(String.valueOf(r));
            }
        }

        return token.toString();
    }
}
