import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.lang.Math;

public class Main{
	final static int WIDTH = 1150;
	final static int HEIGHT = 650;
	final static Vector SIZE = new Vector(WIDTH, HEIGHT);
	static JFrame window;
	static Screen screen;
	static Listener listener;
	static Solver solver;
	
	public static long startTime;
	public static long timeElapsed;
	static long lastFrame;
	final static int DIVISIONS = 100;
	final static int POINTS = 4000;
	
	static Random random = new Random();
	
	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		lastFrame = startTime;
		
		window = new JFrame("Chaos Equations");
        window.setSize(WIDTH, HEIGHT);
        window.setLocation(0,0);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBackground(Color.BLACK);
        
        solver = new Solver();
        screen = new Screen();
        window.setContentPane(screen);
        
        listener = new Listener(screen);
		screen.addKeyListener(listener);
		screen.setDoubleBuffered(true);
		
        window.setVisible(true);
        restart();
	}
	public static void restart() {
		for(int i = 0; i < POINTS; i++) {
			new Point();
		}
	}
	public static void run() {
		//System.out.println("Run");
		double dt = (double)(System.currentTimeMillis()-lastFrame)/1000;
		for(int i = 0; i<DIVISIONS; i++) {
			solver.solve(dt/(double)DIVISIONS);
		}
		screen.repaint();
		lastFrame = System.currentTimeMillis();
		timeElapsed = lastFrame-startTime;
	}
	
}