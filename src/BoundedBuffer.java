class BoundedBuffer<T> {
    private final T[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int size) {
        buffer = (T[]) new Object[size];
    }

    public synchronized void put(T item) throws InterruptedException {
        while (count == buffer.length) {
            System.out.println("PUT WAIT");
            wait();
        }

        buffer[in] = item;
        in = (in + 1) % buffer.length;
        count++;
        System.out.printf("PUT item. COUNT: %d\n", count);

        System.out.println();
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (count == 0) {
            System.out.println("TAKE WAIT");
            wait();
        }

        T item = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.printf("\nTAKE item: %s.\n After take item. COUNT: %d\n", item, count);

        notifyAll();
        System.out.println();
        return item;
    }
}