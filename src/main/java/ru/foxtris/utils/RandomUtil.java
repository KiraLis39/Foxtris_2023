package ru.foxtris.utils;

import java.util.Random;

public class RandomUtil {
    private static final Random r = new Random();

    public static int getNextInt() {
        return r.nextInt(8);
    }

    public static int getNextInt(int bound) {
        return r.nextInt(bound);
    }

    private RandomUtil() {
    }
}
