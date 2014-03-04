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
    
    public NBodyGraphics(NBodyBruteForce problem, double maxDimension, double width, double height) {
        super();
        this.problem = problem;
        this.maxDimension = maxDimension;
        this.width = width;
        this.height = height;
        bodies = problem.getBodies();
    }
    
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        g.setColor(Color.red);
        bodies = problem.getBodies();
        for (Body body : bodies) {
            g.drawRect(convertXCoord(body.getPosition().getX()), 
                   convertYCoord(body.getPosition().getY()), 10, 10);
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
}
