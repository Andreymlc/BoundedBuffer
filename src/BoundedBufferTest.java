import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;


public class BoundedBufferTest extends JFrame {
    private static final int BUFFER_SIZE = 10;
    private int producerCount = 5;
    private int consumerCount = 5;
    private int workInterval = 1000;

    private JPanel producerPanel;
    private JPanel consumerPanel;
    private JPanel bufferPanel;
    private JLabel[] bufferCells;

    public BoundedBufferTest() {
        setTitle("Bounded Buffer Simulation");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Панель управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 2, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Настройки"));

        controlPanel.add(new JLabel("Производители:"));
        JSpinner producerSpinner = new JSpinner(new SpinnerNumberModel(producerCount, 1, 20, 1));
        controlPanel.add(producerSpinner);

        controlPanel.add(new JLabel("Потребители:"));
        JSpinner consumerSpinner = new JSpinner(new SpinnerNumberModel(consumerCount, 1, 20, 1));
        controlPanel.add(consumerSpinner);

        controlPanel.add(new JLabel("Интервал работы (мс):"));
        JSpinner workIntervalSpinner = new JSpinner(new SpinnerNumberModel(workInterval, 100, 5000, 100));
        controlPanel.add(workIntervalSpinner);

        add(controlPanel, BorderLayout.NORTH);

        bufferPanel = new JPanel(new GridLayout(1, BUFFER_SIZE, 5, 5));
        bufferPanel.setBorder(BorderFactory.createTitledBorder("Буфер"));
        bufferCells = new JLabel[BUFFER_SIZE];
        for (int i = 0; i < BUFFER_SIZE; i++) {
            bufferCells[i] = new JLabel();
            bufferCells[i].setOpaque(true);
            bufferCells[i].setBackground(Color.LIGHT_GRAY); // свободная ячейка
            bufferCells[i].setPreferredSize(new Dimension(30, 30));
            bufferPanel.add(bufferCells[i]);
        }
        add(bufferPanel, BorderLayout.CENTER);

        producerPanel = new JPanel(new GridLayout(1, producerCount, 5, 5));
        producerPanel.setBorder(BorderFactory.createTitledBorder("Производители"));
        add(producerPanel, BorderLayout.WEST);

        consumerPanel = new JPanel(new GridLayout(1, consumerCount, 5, 5));
        consumerPanel.setBorder(BorderFactory.createTitledBorder("Потребители"));
        add(consumerPanel, BorderLayout.EAST);

        JButton startButton = new JButton("Запуск");
        startButton.addActionListener(e -> {
            producerCount = (int) producerSpinner.getValue();
            consumerCount = (int) consumerSpinner.getValue();
            workInterval = (int) workIntervalSpinner.getValue();
            startSimulation();
        });
        add(startButton, BorderLayout.SOUTH);
    }

    private void startSimulation() {
        Random random = new Random();
        final BoundedBuffer<Integer> buffer = new BoundedBuffer<>(BUFFER_SIZE);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(producerCount + consumerCount);

        producerPanel.removeAll();
        consumerPanel.removeAll();

        for (int i = 0; i < producerCount; i++) {
            JLabel producerLabel = new JLabel("P-" + i, SwingConstants.CENTER);
            producerLabel.setOpaque(true);
            producerLabel.setBackground(Color.GRAY);
            producerPanel.add(producerLabel);

            new Thread(() -> {
                try {
                    startSignal.await();
                    for (int j = 0; j < 5; j++) {
                        buffer.put(random.nextInt() % 100, producerLabel);
                        updateBufferDisplay(buffer);
                        Thread.sleep(workInterval);
                        producerLabel.setBackground(Color.GRAY); // простой
                        Thread.sleep(workInterval);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                    producerLabel.setBackground(Color.LIGHT_GRAY);
                }
            }, "Producer-" + i).start();
        }

        for (int i = 0; i < consumerCount; i++) {
            JLabel consumerLabel = new JLabel("C-" + i, SwingConstants.CENTER);
            consumerLabel.setOpaque(true);
            consumerLabel.setBackground(Color.GRAY);
            consumerPanel.add(consumerLabel);

            new Thread(() -> {
                try {
                    startSignal.await();
                    for (int j = 0; j < 5; j++) {
                        buffer.take(consumerLabel);
                        updateBufferDisplay(buffer);
                        Thread.sleep(workInterval);
                        consumerLabel.setBackground(Color.GRAY);
                        Thread.sleep(workInterval);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                    consumerLabel.setBackground(Color.LIGHT_GRAY);
                }
            }, "Consumer-" + i).start();
        }

        producerPanel.revalidate();
        consumerPanel.revalidate();

        System.out.println("Запуск потоков!");
        startSignal.countDown();

        new Thread(() -> {
            try {
                doneSignal.await();
                System.out.println("Все потоки завершили работу!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void updateBufferDisplay(BoundedBuffer<?> buffer) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < BUFFER_SIZE; i++) {
                bufferCells[i].setBackground(i < buffer.getCount() ? Color.GREEN : Color.LIGHT_GRAY);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoundedBufferTest app = new BoundedBufferTest();
            app.setVisible(true);
        });
    }
}
