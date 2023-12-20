package uni.makarov.parallel_processing.lab1.block;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


public class Cells1 {
    public static AtomicInteger[] cells;
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

        cells = new AtomicInteger[N];
        barrier = new CyclicBarrier(K, Cells1::print);
        cells[0] = new AtomicInteger(K);
        for (int i = 1; i < cells.length; i++) {
            cells[i] = new AtomicInteger(0);
        }

        for (int i = 0; i < K; i++) {
            new Atom().start();
        }
    }

    private static void print() {
        AtomicInteger[] crystalSnapshot = new AtomicInteger[N];
        for (int i = 0; i < N; i++) {
            crystalSnapshot[i] = new AtomicInteger();
            crystalSnapshot[i].set(cells[i].get());
        }
        AtomicInteger sum = new AtomicInteger(0);
        for (AtomicInteger atomicInteger : crystalSnapshot) {
            sum.getAndAdd(atomicInteger.get());
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

                if (m > Cells1.p) {
                    if (currentPosition < Cells1.N - 1) {
                        synchronized (Cells1.cells) {
                            Cells1.cells[currentPosition].getAndDecrement();
                            currentPosition++;
                            Cells1.cells[currentPosition].getAndIncrement();
                        }
                    }
                } else {
                    if (currentPosition > 0) {
                        synchronized (Cells1.cells) {
                            Cells1.cells[currentPosition].getAndDecrement();
                            currentPosition--;
                            Cells1.cells[currentPosition].getAndIncrement();
                        }
                    }
                }

                endTime = System.currentTimeMillis();
            } while(endTime - startTime < 1000);


            try {
                Cells1.barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
