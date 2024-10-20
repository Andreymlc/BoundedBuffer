import java.util.concurrent.CountDownLatch;

public class BoundedBufferTest {
    public static void main(String[] args) throws InterruptedException {
        final int COUNT_THREADS = 100;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch finishSignal = new CountDownLatch(COUNT_THREADS);

        for (int i = 0; i < COUNT_THREADS; i++) {
            if (i < COUNT_THREADS / 2){
                System.out.println("CREATE PUT TREAD");
                new Thread(new SynchronizedPicker(false, startSignal, finishSignal)).start();
            } else {
                System.out.println("CREATE TAKE TREAD");
                new Thread(new SynchronizedPicker(true, startSignal, finishSignal)).start();
            }
        }

        System.out.println("Запуск потоков!\n");
        startSignal.countDown();
        finishSignal.await();
        System.out.println("\nВсе потоки завершили работу!");
    }
}