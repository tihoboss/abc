import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Random;

public class Main {
    private static final String letters = "abc";
    private static final int length = 100_000;
    ;
    private static final int numQueue = 100;
    private static final BlockingQueue<String> queA = new ArrayBlockingQueue<>(numQueue);
    private static final BlockingQueue<String> queB = new ArrayBlockingQueue<>(numQueue);
    private static final BlockingQueue<String> queC = new ArrayBlockingQueue<>(numQueue);

    public static void main(String[] args) throws InterruptedException {
        Thread generatorThread = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    String text = generateText(letters, length);
                    queA.put(text);
                    queB.put(text);
                    queC.put(text);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread countA = letterCount(queA, 'a');
        Thread countB = letterCount(queB, 'b');
        Thread countC = letterCount(queC, 'c');

        generatorThread.start();
        countA.start();
        countB.start();
        countC.start();

        countA.join();
        countB.join();
        countC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread letterCount(BlockingQueue<String> queue, char symbol) {
        return new Thread(() -> {
            int max = maxCount(queue, symbol);
            System.out.println("Максимальное значение " + symbol + ": " + max);
        });
    }

    public static int maxCount(BlockingQueue<String> queue, char symbol) {
        int count = 0;
        int maxCount = 0;

        try {
            String text = queue.take();
            for (char c : text.toCharArray()) {
                if (c == symbol) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return maxCount;
    }
}
