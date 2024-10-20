import java.util.concurrent.CountDownLatch;

public class SynchronizedPicker implements Runnable{
    private final boolean method;
    private final CountDownLatch startSignal;
    private final CountDownLatch finishSignal;
    private static final BoundedBuffer<String> buffer = new BoundedBuffer<>(10);;

    public SynchronizedPicker(boolean method, CountDownLatch startSignal, CountDownLatch finishSignal) {
        this.method = method;
        this.startSignal = startSignal;
        this.finishSignal = finishSignal;
    }

    @Override
    public void run() {
        try {
            startSignal.await();

            if (method){
                for (int i = 0; i < 3; i++) {
                    buffer.put("Объект");
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    buffer.take();
                }
            }

            finishSignal.countDown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
