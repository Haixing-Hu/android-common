////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.android.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskUtils {

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

  /**
   * Executes a given task asynchronously with a possible delay, returning a
   * {@link CompletableFuture}.
   * <p>
   * This method allows for the execution of a task in a separate thread,
   * followed by a possible delay, and then provides the result via a
   * {@link CompletableFuture}. If an exception is thrown during task execution,
   * it is propagated through the {@link CompletableFuture}.
   * <p>
   * The total number of delay depends on the specified minimum execution time
   * of the task. If the task execution time is less than the specified minimum
   * execution time, the delay will be the difference between the minimum
   * execution time and the task execution time. Otherwise, the delay will be
   * zero.
   * <p>
   * Usage example:
   * <pre><code>
   * executeWithDelay(
   *     () -> {
   *         // Task logic that returns a result
   *         return "Result";
   *     },
   *     2000 // Min execution time in milliseconds
   * ).thenAccept(result -> {
   *     // Handle the result
   *     System.out.println("Task completed with result: " + result);
   * }).exceptionally(e -> {
   *     // Handle any exceptions thrown by the task
   *     e.printStackTrace();
   *     return null;
   * });
   * </code></pre>
   *
   * @param <T>
   *     The type of the result produced by the task.
   * @param task
   *     The task to be executed. This is a {@link Callable} that can return a
   *     result.
   * @param minExecutionTime
   *     The specified minimum execution time of the task, in milliseconds. If
   *     the task execution time is less than the specified minimum execution
   *     time, the delay will be the difference between the minimum execution
   *     time and the task execution time. Otherwise, the delay will be zero.
   * @return
   *     A {@link CompletableFuture} that will complete with the result of the
   *     task after the specified delay. If the task throws an exception, the
   *     {@link CompletableFuture} will complete exceptionally with that exception.
   * @author Haixing Hu
   */
  public static <T> CompletableFuture<T> executeWithDelay(final Callable<T> task,
      final long minExecutionTime) {
    final CompletableFuture<T> future = new CompletableFuture<>();
    EXECUTOR_SERVICE.submit(() -> {
      try {
        final long startTime = System.currentTimeMillis();
        final T result = task.call();
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < minExecutionTime) {
          Thread.sleep(minExecutionTime - elapsedTime);
        }
        future.complete(result);
      } catch (final Exception e) {
        future.completeExceptionally(e);
      }
    });
    return future;
  }


  /**
   * Executes a given task asynchronously with a possible delay, returning a
   * {@link CompletableFuture}.
   * <p>
   * This method allows for the execution of a task in a separate thread,
   * followed by a possible delay, and then provides the result via a
   * {@link CompletableFuture}. If an exception is thrown during task execution,
   * it is propagated through the {@link CompletableFuture}.
   * <p>
   * The total number of delay depends on the specified minimum execution time
   * of the task. If the task execution time is less than the specified minimum
   * execution time, the delay will be the difference between the minimum
   * execution time and the task execution time. Otherwise, the delay will be
   * zero.
   * <p>
   * Usage example:
   * <pre><code>
   * executeWithDelay(
   *     () -> {
   *         // Task logic that has no return
   *     },
   *     2000 // Min execution time in milliseconds
   * ).thenAccept(() -> {
   *     // Handle the result
   *     System.out.println("Task completed without result.");
   * }).exceptionally(e -> {
   *     // Handle any exceptions thrown by the task
   *     e.printStackTrace();
   *     return null;
   * });
   * </code></pre>
   *
   * @param <T>
   *     The type of the result produced by the task.
   * @param task
   *     The task to be executed. This is a {@link Runnable} that has no return.
   * @param minExecutionTime
   *     The specified minimum execution time of the task, in milliseconds. If
   *     the task execution time is less than the specified minimum execution
   *     time, the delay will be the difference between the minimum execution
   *     time and the task execution time. Otherwise, the delay will be zero.
   * @return
   *     A {@link CompletableFuture} that will complete with the result of the
   *     task after the specified delay. If the task throws an exception, the
   *     {@link CompletableFuture} will complete exceptionally with that exception.
   * @author Haixing Hu
   */
  public static <T> CompletableFuture<T> executeWithDelay(final Runnable task,
      final long minExecutionTime) {
    return executeWithDelay(Executors.callable(task, null), minExecutionTime);
  }
}
