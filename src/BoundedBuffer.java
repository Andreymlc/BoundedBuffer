import javax.swing.*;
import java.awt.*;

class BoundedBuffer<T> {
    private final T[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int size) {
        buffer = (T[]) new Object[size];
    }

    public synchronized void put(T item, JLabel layout) throws InterruptedException {
        while (count == buffer.length) {
            System.out.println("PUT WAIT");
            layout.setBackground(Color.RED);
            wait();
        }

        layout.setBackground(Color.GREEN);
        buffer[in] = item;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("\nPUT - " + item + "\nCount: " + count);
        System.out.println();
        Thread.sleep(200);

        notifyAll();
    }

    public synchronized T take(JLabel layout) throws InterruptedException {
        while (count == 0) {
            System.out.println("TAKE WAIT");
            layout.setBackground(Color.RED);
            wait();
        }
        layout.setBackground(Color.GREEN);

        T item = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println("\nTook - " + item + "\nCount: " + count);
        System.out.println();
        Thread.sleep(200);

        notifyAll();
        return item;
    }

    public int getCount() {
        return count;
    }
}