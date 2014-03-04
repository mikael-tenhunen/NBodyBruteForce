package nbodybruteforce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

/**
 * Optional command-line arguments: 
 * 1. number of bodies 
 * 2. number of time steps
 * 3. number of threads 
 * 4. min mass of bodies 
 * 5. max mass of bodies 
 * 6. max starting velocity component of bodies
 */
public class NBodyBruteForce {

    public final double G = 6.67384E-11;
    public static final int timeStep = 1;
    int n;
    int timeSteps;
    int procs;
    Point2D.Double[][] forceMatrix;
    private Body[] bodies;

    public NBodyBruteForce(int n, int timeSteps, int procs, Body[] bodies) {
        this.n = n;
        this.timeSteps = timeSteps;
        this.procs = procs;
        this.bodies = bodies;
        forceMatrix = new Point2D.Double[procs][n];
        for (int i = 0; i < procs; i++) {
            for (int j = 0; j < n; j++) {
                forceMatrix[i][j] = new Point2D.Double(0, 0);
            }
        }
    }

    public void calculateForces(int workerNr) {
        double distance;
        double magnitude;
        Point2D.Double direction;
        double directionX, directionY, forceX, forceY;
        Body leftBody;
        Body rightBody;
        Point2D.Double force;
        /*       
         workerNr is an index for row in forceMatrix
         i and j identify the pair of bodies being processed
         i is index for column, left body
         j is index for column, right body
         */
        for (int i = workerNr; i < n; i += procs) {
            for (int j = i + 1; j < n; j++) {
                leftBody = bodies[i];
                rightBody = bodies[j];
                distance = leftBody.getPosition().distance(rightBody.getPosition());
                if (distance > 10E-3) {
                    magnitude = G * leftBody.getMass() * rightBody.getMass();
                    directionX = rightBody.getPosition().getX() - leftBody.getPosition().getX();
                    directionY = rightBody.getPosition().getY() - leftBody.getPosition().getY();
                    forceX = (magnitude * directionX) / distance;
                    forceY = (magnitude * directionY) / distance;
                    force = forceMatrix[workerNr][i];
                    force.setLocation(force.getX() + forceX, force.getY() + forceY);
                    force = forceMatrix[workerNr][j];
                    force.setLocation(force.getX() - forceX, force.getY() - forceY);
                }
            }
        }
    }

    void moveBodies(int workerNr) {
        Point2D.Double deltav = new Point2D.Double();
        Point2D.Double deltap = new Point2D.Double();
        Point2D.Double force = new Point2D.Double();
        Point2D.Double velocity;
        Point2D.Double position;
        Body currBody;

        for (int i = workerNr; i < n; i += procs) {
            //combine forces
            for (int k = 1; k < procs; k++) {
                force.setLocation(force.getX() + forceMatrix[k][i].getX(),
                        force.getY() + forceMatrix[k][i].getY());
            }
            //move bodies
            currBody = bodies[i];
            deltav.setLocation(force.getX() / currBody.getMass() * timeStep,
                    force.getY() / currBody.getMass() * timeStep);
            deltap.setLocation(currBody.getVelocity().getX() + deltav.getX() / 2 * timeStep,
                    currBody.getVelocity().getY() + deltav.getY() / 2 * timeStep);
            velocity = currBody.getVelocity();
            velocity.setLocation(velocity.getX() + deltav.getX(),
                    velocity.getY() + deltav.getY());
            position = currBody.getPosition();
            position.setLocation(position.getX() + deltap.getX(),
                    position.getY() + deltap.getY());
            //Reset force
            force.setLocation(0, 0);
        }
    }
    
    public Body[] getBodies() {
        return bodies;
    }    

    /**
     * @param args the command line arguments 1. number of bodies 2. number of
     * time steps 3. number of threads 4. min mass of bodies 5. max mass of
     * bodies 6. max starting velocity component of bodies
     */
    public static void main(String[] args) throws InterruptedException {
        int n = 2;
        int timeSteps = 350000000;
        int procs = 1;
        double minMass = 10E20;
        double maxMass = 10E20;
        double maxStartVelComponent = 0.1;
        double maxDimension = 1000000;
        double height = 800;
        double aspectRatio = 1;
        long startTime;
        long endTime;
        //read command-line arguments
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            timeSteps = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            procs = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            minMass = (double) Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            maxMass = (double) Integer.parseInt(args[4]);
        }
        if (args.length > 5) {
            maxStartVelComponent = (double) Integer.parseInt(args[5]);
        }
        //initialize bodies
        Body[] bodies = new Body[n];
        double posX, posY, velX, velY, mass;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            posX = random.nextDouble() * maxDimension * aspectRatio;
            posY = random.nextDouble() * maxDimension;
            velX = random.nextDouble() * maxStartVelComponent;
            velY = random.nextDouble() * maxStartVelComponent;
            mass = random.nextDouble() * (maxMass - minMass) + minMass;
            bodies[i] = new Body(new Point2D.Double(posX, posY),
                    new Point2D.Double(velX, velY), mass);
        }
        //initialize object representing n-body problem
        NBodyBruteForce nBodyProblem = new NBodyBruteForce(n, timeSteps, procs, bodies);
        //show parameters
        System.out.println("n: " + n);
        System.out.println("ticks (at " + NBodyBruteForce.timeStep + "): " + timeSteps);
        System.out.println("workers: " + procs);
        //initiate graphics
        double width = height * aspectRatio;
        JFrame frame = new JFrame();
        NBodyGraphics graphics = new NBodyGraphics(nBodyProblem, maxDimension, 
               width, height);
        frame.setPreferredSize(new Dimension((int) width, (int) height));
        frame.setSize(new Dimension((int) width, (int) height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphics.setBackground(Color.black);
        frame.add(graphics, BorderLayout.CENTER);
        frame.setVisible(true);
        graphics.repaint();
        //thread control
        CyclicBarrier barrier = new CyclicBarrier(procs);
        ExecutorService executor = Executors.newFixedThreadPool(procs);
        //start simulation
        startTime = System.nanoTime();
        for (int i = 0; i < procs; i++) {
            executor.execute(new Worker(i, nBodyProblem, barrier, timeSteps, graphics));
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.DAYS);
        endTime = System.nanoTime();
        System.out.println("Time: " + (endTime - startTime) * 10E-10 + " seconds");
    }
}
