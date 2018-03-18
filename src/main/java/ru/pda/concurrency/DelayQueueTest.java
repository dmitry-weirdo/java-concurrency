package ru.pda.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class DelayQueueTest {

    public static void main(final String[] args) throws InterruptedException {


        // can add only java.util.concurrent.Delayed to DelayQueue, this will throw a ClassCastException
//        queue.put( new Object() ); // BlockingQueue.put throws InterruptedException

        final Delayed delayed100 = new MyDelayed1(1000, "my delayed 1000 ms");
        final Delayed delayed500 = new MyDelayed1(5000, "my delayed 5000 ms");
        final Delayed delayed0 = new MyDelayed1(0, "my delayed 0 ms (immediately expired)");

        final DelayQueue<Delayed> queue = new DelayQueue<>();

        final Thread thread = new QueueReadingThread(queue);
        thread.start();

        System.out.println("Staring to put to the queue...");
        queue.put(delayed500);
        queue.put(delayed100);
        queue.put(delayed0);
        System.out.println("Finished putting to the queue.");

        Thread.sleep(7000);
        thread.interrupt();
    }

    static class MyDelayed1 implements Delayed {
        private final long millisecondsDelay;
        private final long expirationTimeMillis;
        private final String value;

        public MyDelayed1(final long millisecondsDelay, final String value) {
            this.millisecondsDelay = millisecondsDelay;
            this.value = value;
            this.expirationTimeMillis = System.currentTimeMillis() + this.millisecondsDelay;

            System.out.printf("Name: %s; expiration time millis: %d\n", this.value, this.expirationTimeMillis);
        }

        public String getValue() {
            return value;
        }

        @Override
        public long getDelay(final TimeUnit unit) { // this must return a dynamically changing delay, not a constant
            final long remainingDelay = expirationTimeMillis - System.currentTimeMillis();
            return unit.convert(remainingDelay, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(final Delayed o) { /// !!! this implementation must be consistent with #getDelay() implementation. Otherwise the DelayQueue "order by delay" logic won't work. @see Delayed javadoc
            return Long.compare( this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS) );
        }
    }

    static class QueueReadingThread extends Thread {
        private final BlockingQueue<Delayed> queue;

        public QueueReadingThread(final BlockingQueue<Delayed> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("========================");
                    System.out.println("Taking from the queue...");
                    final MyDelayed1 element = (MyDelayed1) queue.take();
                    System.out.printf("Reading thread: taken %s from thread\n", element.getValue());
                }
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}