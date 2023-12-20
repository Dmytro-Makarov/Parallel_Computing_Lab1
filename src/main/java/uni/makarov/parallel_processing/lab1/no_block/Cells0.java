package uni.makarov.parallel_processing.lab1.no_block;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Cells0 {
    public static Integer[] cells;
    public static CyclicBarrier barrier;
    public static int N; //кількість клітинок
    public static int K; //кількість атомів домішок
    public static double p; //вірогідність переходу праворуч


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть N: ");
        N = scanner.nextInt();
        System.out.print("Введіть K: ");
        K = scanner.nextInt();
        System.out.print("Введіть p: ");
        p = scanner.nextDouble();
        scanner.close();

        cells = new Integer[N];
        barrier = new CyclicBarrier(K, Cells0::print);
        cells[0] = K;
        for (int i = 1; i < cells.length; i++) {
            cells[i] = 0;
        }

        for (int i = 0; i < K; i++) {
            new Atom().start();
        }
    }

    private static void print() {
        Integer[] crystalSnapshot = new Integer[N];
        System.arraycopy(cells, 0, crystalSnapshot, 0, N);
        int sum = 0;
        for (Integer integer : crystalSnapshot) {
            sum += integer;
        }
        System.out.println(Arrays.toString(crystalSnapshot));
        System.out.println("Кількість атомів: " + sum);
        System.out.println();
    }
}

class Atom extends Thread {
    @Override
    public void run() {
        super.run();
        int currentPosition = 0;
        for (int j = 0; j < 5; j++) {
            long endTime;
            long startTime = System.currentTimeMillis();
            do {
                double m = Math.random();

                if (m > Cells0.p) {
                    if (currentPosition < Cells0.N - 1) {
                        Cells0.cells[currentPosition]--;
                        currentPosition++;
                        Cells0.cells[currentPosition]++;
                    }
                } else {
                    if (currentPosition > 0) {
                        Cells0.cells[currentPosition]--;
                        currentPosition--;
                        Cells0.cells[currentPosition]++;
                    }
                }
                endTime = System.currentTimeMillis();
            } while(endTime - startTime < 1000);

            try {
                Cells0.barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
