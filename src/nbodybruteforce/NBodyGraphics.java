package nbodybruteforce;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class NBodyGraphics extends JPanel {
    NBodyBruteForce problem;
    Body[] bodies;
    double maxDimension;
    double width;
    double height;
    double invertedMaxMass;
    
    public NBodyGraphics(NBodyBruteForce problem, double maxDimension, double width, 
            double height, double maxMass) {
        super();
        this.problem = problem;
        this.maxDimension = maxDimension;
        this.width = width;
        this.height = height;
        invertedMaxMass = 1 / maxMass;
        bodies = problem.getBodies();
    }
    
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        g.setColor(Color.white);
        bodies = problem.getBodies();
        int size;
        int halfSize;
        for (Body body : bodies) {
            size = convertMass(body.getMass());
            halfSize = size / 2;
            g.drawOval(convertXCoord(body.getPosition().getX()) - halfSize, 
                   convertYCoord(body.getPosition().getY()) - halfSize, 
                   size, size);
//            System.out.println("x: " + convertXCoord(body.getPosition().getX()) + 
//                    "\ny: " + convertYCoord(body.getPosition().getY()));
//            System.out.println("x: " + body.getPosition().getX() + 
//                    "\ny: " + body.getPosition().getY());
        }
    }  
    
    public int convertXCoord(double coord) {
        return (int) ((coord / maxDimension) * width);
    }
    
    public int convertYCoord(double coord) {
        return (int) ((coord / maxDimension) * height);
    }    
    
    public int convertMass(double mass) {
        return (int) (mass * 10 * invertedMaxMass) + 1;
    }
}
