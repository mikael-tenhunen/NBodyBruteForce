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

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        //g.setColor(Color.white);
        bodies = problem.getBodies();
        int size;
        int halfSize;
        for (Body body : bodies) {
            size = convertMass(body.getMass());
            if (size < 3) {
                g.setColor(Color.getHSBColor(0.0f, 0.15f, 1f));
            } else if (size < 6) {
                g.setColor(Color.getHSBColor(0, 0.50f, 1f));
            } else if (size < 9) {
                g.setColor(Color.getHSBColor(0, 0.84f, 1f));
            } else if (size < 12) {
                g.setColor(Color.getHSBColor(0, 1.0f, 1f));
            }
            halfSize = size / 2;
            g.drawOval(convertXCoord(body.getPosition().getX()) - halfSize,
                    convertYCoord(body.getPosition().getY()) - halfSize,
                    size, size);
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
