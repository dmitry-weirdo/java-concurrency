package ru.pda.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("num of processors: " + Runtime.getRuntime().availableProcessors());


        final Thread thread = new Thread(() -> {
            try {
                System.out.println( Thread.currentThread().getName() + " - before sleep" );
                Thread.sleep(5000);
                System.out.println( Thread.currentThread().getName() + " - after sleep" );
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // a long activity
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i < 1005000; i++) {
                list.add(i * 2);
            }
            System.out.println("list.size: + " + list.size());
        });

        final Thread thread2 = new Thread(() -> {
            System.out.println("Thread2: joining to thread1");
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Thread2 execution");
        });


//        wait();
//        notify();

        System.out.println("Thread state: " + thread.getState()); // NEW
        thread.start();
        System.out.println("Thread state: " + thread.getState()); // RUNNABLE

        System.out.println("Thread2 state: " + thread2.getState()); // NEW
        thread2.start();
        System.out.println("Thread2 state: " + thread2.getState()); // RUNNABLE

        Thread.sleep(2000);
        System.out.println("Thread state: " + thread.getState()); // TIMED_WAITING

        thread.interrupt();
        System.out.println("Thread interrupted: " + thread.isInterrupted());
        System.out.println("Thread state: " + thread.getState()); // TIMED_WAITING
        Thread.sleep(10);
        System.out.println("222 Thread state: " + thread.getState()); // RUNNABLE
        System.out.println("222 Thread interrupted: " + thread.isInterrupted()); // false

        Thread.sleep(5000);
        System.out.println("Thread state: " + thread.getState()); // TERMINATED
        System.out.println("Thread is alive: " + thread.isAlive());

        thread.interrupt();
        System.out.println("Thread interrupted: " + thread.isInterrupted()); // interruption of a dead thread is ignored

        Collections.synchronizedList(new ArrayList<>());
    }
}