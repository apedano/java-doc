import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class VirtualThreadsFeatureExample {

    private static void runSingleVirtualThreads() throws InterruptedException {
        Thread vThread1 = Thread.ofVirtual().start(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Virtual Thread 1: " + i);
            }
        });

        Thread vThread2 = Thread.ofVirtual().start(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Virtual Thread 2: " + i);
            }
        });
        vThread1.join();
        vThread2.join();

    }

    private static void runVirtualThreadsWithExecutor() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    System.out.println(Thread.currentThread().getName() + " returning " + i);
                    return i;
                });
            });
        }  // executor.close() is called implicitly, and waits
    }
    public static void main(String[] args) throws InterruptedException {
        runSingleVirtualThreads();
        runVirtualThreadsWithExecutor();
    }
}
