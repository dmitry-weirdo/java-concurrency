package ru.pda.concurrency;

import java.util.concurrent.Exchanger;

public final class ExchangerTest {

    public static void main(final String[] args) {
        final Exchanger<String> exchanger = new Exchanger<>();

        final ExchangerThread t1 = new ExchangerThread(exchanger, 1000, "value from t1");
        t1.setName("t1");

//        final ExchangerThread t2 = new ExchangerThread(exchanger, 5000, "value from t2");
        final ExchangerThread t2 = new ExchangerThread(exchanger, 5000, null); // null value works ok
        t2.setName("t2");

        t1.start();
        t2.start();
    }

    private static class ExchangerThread extends Thread {
        private final Exchanger<String> exchanger;
        private final long delayMilliseconds;
        private final String value;

        public ExchangerThread(final Exchanger<String> exchanger, final long delayMilliseconds, final String value) {
            this.exchanger = exchanger;
            this.delayMilliseconds = delayMilliseconds;
            this.value = value;
        }

        @Override
        public void run() {
            try {
                ThreadUtils.logWithCurrentThreadName("Starting thread...");

                Thread.sleep(delayMilliseconds);

                ThreadUtils.logWithCurrentThreadName( String.format("Exchange the value \"%s\"...", value) );
                final String valueFromOtherThread = exchanger.exchange(value);
                ThreadUtils.logWithCurrentThreadName( String.format("Exchanged my value \"%s\" with other thread value \"%s\".", value, valueFromOtherThread) );
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}