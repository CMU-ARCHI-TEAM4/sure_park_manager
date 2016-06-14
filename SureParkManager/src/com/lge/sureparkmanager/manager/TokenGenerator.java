package com.lge.sureparkmanager.manager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public final class TokenGenerator {
    private static DateFormat mDateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");

    public static String generateToken() {
        StringBuilder token = new StringBuilder();

        token.append(mDateFormat.format(new Date()));
        for (int i = 0; i < 6; i++) {
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
