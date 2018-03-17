package ru.pda.concurrency;

public class WaitTest {
    private static final Object lock = new Object();

    public static void main(final String[] args) {
        final WaitTestObject object = new WaitTestObject();

        final Thread t1 = new Thread(object::doWait);

        t1.setName("t1");
        t1.start();

        final Thread t2 = new Thread(() -> {
            object.doNotify(t1);
        });

        t2.setName("t2");
        t2.start();;
    }

    private static void logWithCurrentThreadName(final String string) {
        System.out.printf("[%s]: %s\n", Thread.currentThread().getName(), string);
    }

    static class WaitTestObject {
        public void doWait() {

            synchronized (lock) {
                try {
                    logWithCurrentThreadName("doWait: before wait");
                    lock.wait(); // this will release lock monitor
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                logWithCurrentThreadName("doWait: notified!. Current thread state: " + Thread.currentThread().getState()); // RUNNABLE
            }
        }

        public void doNotify(final Thread waitingThread) {
            synchronized (lock) {
                logWithCurrentThreadName("doNotify: before notify(). Waiting thread state: " + waitingThread.getState()); // t1 state will be WAITING
                lock.notify(); // notify does not throw InterruptedException

                logWithCurrentThreadName("doNotify: called notify(). Waiting thread state: " + waitingThread.getState()); // t1 state will be BLOCKED since it's waiting for lock monitor to be released
                logWithCurrentThreadName("Sleeping...");

                try {
                    Thread.sleep(3000);
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                logWithCurrentThreadName("doNotify: sleep finished. Existing the synchronized block.");
            }

            // wait thread is notified only after notifying thread has released the lock
            logWithCurrentThreadName("doNotify: released lock. Waiting thread state: " + waitingThread.getState()); // BLOCKED (but it will still become RUNNABLE)
        }
    }
}