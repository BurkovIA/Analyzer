import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static Thread textGenerator;

    public static void main(String[] args) throws InterruptedException {
        textGenerator = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String text = generateText("abc", 100000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread a = createNewThread(queueA, 'a');
        Thread b = createNewThread(queueB, 'b');
        Thread c = createNewThread(queueC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();


    }

    private static Thread createNewThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = 0;
            try {
                max = findMaxCharCount(queue, letter);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Максимальное значение " + letter + " это " + max);
        });
    }

    private static int findMaxCharCount(BlockingQueue<String> queue, char letter) throws InterruptedException {
        int max = 0;
        String text;
        try {
            text = queue.take();
            int count = 0;
            for (char c : text.toCharArray()) {
                if (c == letter) {
                    count++;
                }
            }
            if (count > max) {
                max = count;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
            return -1;
        }
        return max;
    }

    private static String generateText(String characters, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }
}