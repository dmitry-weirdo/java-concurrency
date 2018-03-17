package ru.pda.concurrency;

public final class InheritableThreadLocalTest {

    public static void main(final String[] args) throws InterruptedException {
        final MyThread thread = new MyThread();
        thread.start();
        thread.join();
    }

    static class MyThread extends Thread {
        private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
        private static final InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

        static final Object lock = new Object();

        @Override
        public void run() {
            threadLocal.set("parent ThreadLocal value");
            inheritableThreadLocal.set("parent InheritableThreadLocal value");

            final MySubThread subThread = new MySubThread();
            subThread.start();

            try {
                Thread.sleep(200);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lock) {
                threadLocal.set("parent ThreadLocal updated value");
                inheritableThreadLocal.set("parent InheritableThreadLocal updated value");
                System.out.println("=========================");
                System.out.println("Parent thread. Updated ThreadLocal values");
                System.out.println("parent thread after parent update. ThreadLocal value: " + threadLocal.get() );
                System.out.println("parent thread after parent update. InheritableThreadLocal value: " + inheritableThreadLocal.get() );
            }

            try {
                subThread.join();
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }

            // does not see any value set by child threads
            System.out.println("=========================");
            System.out.println("parent thread after child thread. ThreadLocal value: " + threadLocal.get() );
            System.out.println("parent thread after child thread. InheritableThreadLocal value: " + inheritableThreadLocal.get() );
        }
    }

    static class MySubThread extends Thread {
        @Override
        public void run() {
            System.out.println("=========================");
            System.out.println( "child thread. ThreadLocal value: " + MyThread.threadLocal.get() );
            System.out.println( "child thread. InheritableThreadLocal value: " + MyThread.inheritableThreadLocal.get() );

            try {
                Thread.sleep(1000);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (MyThread.lock) {
                // does not see values updated  by parent thread. Sees only initial values on starting the child thread
                System.out.println("=========================");
                System.out.println( "child thread after parent thread update. ThreadLocal value: " + MyThread.threadLocal.get() );
                System.out.println( "child thread after parent thread. InheritableThreadLocal value: " + MyThread.inheritableThreadLocal.get() );
            }

            MyThread.threadLocal.set("child ThreadLocal value");
            MyThread.inheritableThreadLocal.set("child InheritableThreadLocal value");

            System.out.println("=========================");
            System.out.println( "child thread after set. ThreadLocal value: " + MyThread.threadLocal.get() );
            System.out.println( "child thread after set. InheritableThreadLocal value: " + MyThread.inheritableThreadLocal.get() );
        }
    }
}