package com.bekaku.api.spring.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.security.SecureRandom;
import java.util.UUID;

public class UuidUtils {
    private static final SecureRandom random = new SecureRandom();

    private UuidUtils() {
    }

    public static UUID randomV7() {
        long unixMillis = System.currentTimeMillis(); // 48-bit timestamp
        int randHi = random.nextInt(1 << 12);         // 12 random bits
        long high = ((unixMillis & 0xFFFFFFFFFFFFL) << 16) // 48 bits to left
                | 0x7000                            // version 7
                | (randHi & 0x0FFF);               // 12 random bits

        long low = random.nextLong();
        low = (low & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L; // variant bits

        return new UUID(high, low);
    }
    public static UUID generateUUID() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
