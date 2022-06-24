import javax.swing.*;
import java.awt.event.*;
import java.awt.Point;
import java.awt.MouseInfo;

public class Listener implements KeyListener, ActionListener{
	public static final int FPS = 30;
	private final Timer timer = new Timer(1000/FPS, this);
	private boolean[] keysPressed = new boolean[2048];
	
	private static Vector MouseXY = new Vector(0,0);
	private static Screen screen;
	
	private static boolean run = true;
	public Listener(Screen s) {
		screen = s;
		timer.start();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		screen.requestFocus();
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		MouseXY.setX((double)mouse.getX());
		MouseXY.setY((double)mouse.getY());
		if(keysPressed[88]) {
			Screen.scale(1.01);
		}
		if(keysPressed[90]) {
			Screen.scale(0.99);
		}
		if(keysPressed[37]) {
			Screen.translate(new Vector(10.0/Screen.getScale(), 0.0));
		}
		if(keysPressed[38]) {
			Screen.translate(new Vector(10.0/Screen.getScale(), 10.0));
		}
		if(keysPressed[39]) {
			Screen.translate(new Vector(-10.0/Screen.getScale(), 0.0));
		}
		if(keysPressed[40]) {
			Screen.translate(new Vector(0.0, -10.0/Screen.getScale()));
		}
		if(run) {
			Main.run();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysPressed[e.getKeyCode()] = true;
		if(e.getKeyCode() == 32) {
			if(run) {
				run = false;
			}
			else {
				run = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed[e.getKeyCode()] = false;
	}
	public static Vector getMouseXY() {
		return MouseXY.clone();
	}
}
