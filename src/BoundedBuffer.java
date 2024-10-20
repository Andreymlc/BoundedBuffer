class BoundedBuffer<T> {
    private final T[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = -1;

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int size) {
        buffer = (T[]) new Object[size];
    }

    public synchronized void put(T item) throws InterruptedException {
        System.out.println();
        while (count == buffer.length) {
            System.out.println("PUT WAIT");
            wait();
        }

        System.out.println("\nPUT item");
        buffer[in] = item;
        count++;
        in++;
        out++;

        System.out.printf("After put item:\nCOUNT: %d\nIN: %d\nOUT: %d\n", count, in, out);

        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        System.out.println();
        while (count == 0) {
            System.out.println("TAKE WAIT");
            wait();
        }

        System.out.println("\nTAKE item");
        T item = buffer[out];
        count--;
        in--;
        out--;

        System.out.println("Took: " + item);
        System.out.printf("After take item:\nCOUNT: %d\nIN: %d\nOUT: %d\n", count, in, out);

        notifyAll();

        return item;
    }
}