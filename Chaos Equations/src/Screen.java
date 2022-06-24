import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

public class Screen extends JPanel{
	private static Vector coordTranslate = new Vector(0.0,0.0);
	private static double scale = 200.0;
	public static final Vector translate = new Vector(Main.WIDTH/2.0,Main.HEIGHT/2.0);
	private static int frameNum = 0;
	public Screen() {
		super();
	}
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g.create();
		if(frameNum%1 == 0) {
			if(!Point.allPoints) {
				g2d.setPaint(new Color(0,0,0,8));
				g2d.fillRect(0,0,this.getWidth(),this.getHeight());
			}
		}
		for(int i = Entity.entities.size()-1; i>=0; i--) {
			Entity.entities.get(i).draw(g2d);
		}
		g2d.dispose();
		frameNum += 1;
	}
	public static void translate(Vector xy) {
		coordTranslate.addToThis(xy);
	}
	public static void scale(double k) {
		scale *= k;
	}
	public static void addScale(double k) {
		scale += k;
	}
	public static double getScale() {
		return scale;
	}
	public static Vector getTranslate() {
		return coordTranslate.clone();
	}
}