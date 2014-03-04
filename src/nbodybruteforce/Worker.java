package nbodybruteforce;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 *
 */
public class Worker implements Runnable {
    private final int workerNr;
    private final int timeSteps;
    private final NBodyBruteForce problem;
    private final CyclicBarrier barrier;
    NBodyGraphics graphics;

    public Worker(int workerNr, NBodyBruteForce problem, CyclicBarrier barrier, int timeSteps, NBodyGraphics graphics) {
        this.workerNr = workerNr;
        this.problem = problem;
        this.barrier = barrier;
        this.timeSteps = timeSteps;
        if (workerNr == 0) {
            this.graphics = graphics;
        }
        else {
            graphics = null;
        }
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < timeSteps; i++) {
//                System.out.println("Worker nr " + workerNr + " enters iteration " + i);
                problem.calculateForces(workerNr);
//                System.out.println("Worker nr " + workerNr + " done calculating forces. Waiting...");
                barrier.await();
//                System.out.println("Worker nr " + workerNr + " will move bodies");
                problem.moveBodies(workerNr);
//                System.out.println("Worker nr " + workerNr + " done moving bodies. Waiting...");
                barrier.await();
                if (workerNr == 0 && graphics != null) {
                    graphics.repaint();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (BrokenBarrierException ex) {
            ex.printStackTrace();
        }
//        System.out.println("Worker nr " + workerNr + " done!");
    }
}
