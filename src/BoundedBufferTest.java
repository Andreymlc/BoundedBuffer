import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class BoundedBufferTest {
    public static void main(String[] args) throws InterruptedException {
        final int BUFFER_SIZE = 10;
        final int PRODUCER_COUNT = 15;
        final int CONSUMER_COUNT = 10;
        Random random = new Random();
        final BoundedBuffer<Integer> buffer = new BoundedBuffer<>(BUFFER_SIZE);

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(PRODUCER_COUNT + CONSUMER_COUNT);

        for (int i = 0; i < PRODUCER_COUNT; i++) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    for (int j = 0; j < 5; j++) {
                        buffer.put(random.nextInt() % 100);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            }, "Producer-" + i).start();
        }

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    for (int j = 0; j < 5; j++) {
                        buffer.take();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            }, "Consumer-" + i).start();
        }

        System.out.println("Запуск потоков!");
        startSignal.countDown();

        doneSignal.await();
        System.out.println("Все потоки завершили работу!");
    }
}