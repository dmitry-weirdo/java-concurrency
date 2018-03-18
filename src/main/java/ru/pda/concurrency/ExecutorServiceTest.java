 package ru.pda.concurrency;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public final class ExecutorServiceTest {

    public static void main(final String[] args) {
        final ExecutorService service = Executors.newFixedThreadPool(10);

        final Callable<String> task1 = () -> {
            Thread.sleep(1000);
            ThreadUtils.logWithCurrentThreadName("Returning from task 1");
            return "Task 1 result";
        };
        final Callable<String> task2 = () -> {
            Thread.sleep(5000);
            ThreadUtils.logWithCurrentThreadName("Returning from task 2");
            return "Task 2 result";
        };
        final Callable<String> task3 = () -> {
            Thread.sleep(3000);
            ThreadUtils.logWithCurrentThreadName("Throwing exception from task 3");
            throw new Exception("Checked exception thrown from task 3");
        };

        List<Future<String>> futureList = Collections.emptyList();

        try {
            System.out.println("Executing invokeAll...");
            futureList = service.invokeAll( List.of(task1, task2, task3) ); // returns same order as the iterator of the original collection
            System.out.println("invokeAll executed");
        }
        catch (final InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Checking future results.");
        for (final Future<String> future : futureList) {
            System.out.println("=======================");
            System.out.printf("Future done: %b, cancelled: %b\n", future.isDone(), future.isCancelled() );

            try {
                final String result = future.get(); // using get() with timeout + TimeoutException has no sense in this case since invokeAll returns all completed tasks
                System.out.printf("Future returned a result: %s\n", result);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
            catch (final ExecutionException e) {
                System.out.println("Future execution has thrown an exception.");
                e.printStackTrace();
            }
        }

        service.shutdown(); // todo: must be performed in "finally" block?
    }
}