package ru.pda.concurrency.fairLock;

public class QueueObject {
    private boolean isNotified = false;

    public synchronized void doWait() throws InterruptedException {
        while ( !isNotified ) { // check the signal
            this.wait();
        }

        this.isNotified = false;
    }

    public synchronized void doNotify() {
        this.isNotified = true; // save the signal so that it won't be lost even if doNotify() is called before doWait()
        this.notify();
    }
}