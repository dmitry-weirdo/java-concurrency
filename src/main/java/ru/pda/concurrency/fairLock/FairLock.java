package ru.pda.concurrency.fairLock;

import java.util.ArrayList;
import java.util.List;

public class FairLock {
    private boolean isLocked = false;
    private Thread lockingThread = null;
    private final List<QueueObject> waitingThreads = new ArrayList<>();

    public void lock() throws InterruptedException {
        final QueueObject queueObject = new QueueObject(); // each lock() call is performed on a separate QueueObject
        synchronized (this) {
            waitingThreads.add(queueObject);
        }

        boolean isLockedForThisThread = true;
        while (isLockedForThisThread) {
            synchronized (this) {
                isLockedForThisThread = isLocked || ( !waitingThreads.get(0).equals(queueObject) );

                if ( !isLockedForThisThread ) { // lock by this thread logic
                    isLocked = true;
                    waitingThreads.remove(queueObject);
                    lockingThread = Thread.currentThread();
                    return;
                }
            }

            try {
                // calls wait on queueObject instance (using it as object monitor).
                // Note this is synchronized on QueueObject, not on Lock (this). This will prevent nested monitor lockout (other thread can call unlock() in this moment and it won't hang.
                queueObject.doWait();
            }
            catch (final InterruptedException e) {
                synchronized (this) {
                    waitingThreads.remove(queueObject); // in this case the calling thread will exit the lock() method, therefore its QueueObject has to be removed from the queue
                }

                throw e;
            }
        }
    }

    public synchronized void unlock() { // can be called only by lock-owning lockingThread
        if ( !this.lockingThread.equals(Thread.currentThread()) ) { // note that Thread does not override equals
            throw new IllegalMonitorStateException("Calling thread has not locked this lock");
        }

        isLocked = false;
        lockingThread = null;

        if ( !waitingThreads.isEmpty() ) {
            waitingThreads.get(0).doNotify(); // call notify() on first QueueObject in the queue. It is synchronized on QueueObject
        }
    }
}