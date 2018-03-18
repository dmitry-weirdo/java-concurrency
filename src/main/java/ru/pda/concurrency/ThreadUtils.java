package ru.pda.concurrency;

public final class ThreadUtils {

    public static void logWithCurrentThreadName(final String string) {
        System.out.printf("[%s]: %s\n", Thread.currentThread().getName(), string);
    }
}