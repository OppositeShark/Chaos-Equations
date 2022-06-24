import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.util.Random;

public class Point extends Entity{
	static private final double TIMESCALE = 0.3;
	
	static private final double timeMin = 1.2;
	static private final double timeMax = 1.0;
	static private final boolean randomTime = false;
	
	static private final boolean reset = true;
	//If the point lives in real time of # of iterations/frames
	static private final boolean lifeIterations = true;
	static private final double lifeSpan = 1.0;
	
	//Calculate all points one by one or just randomly
	public static final boolean allPoints = true;
	//Draw a line connecting the last 2 points or just draw a point
	private static final boolean drawLine = false;
	//Connect the point respectively to the ones last frame (WIP)
	static private final boolean connectPast = false;
	//Draw only the first to final point
	static private final boolean drawFinal = false;
	//Take the output of the function as the derivative or the xy coords
	static private final boolean derivative = false;
	
	
	static double a = 0.108234;
	static double b = -2.337;
	static double c = 1;
	static double d = 1.378;
	
	private Vector calcNext() {
		double t = this.t;
		double x = this.getX();
		double y = this.getY();
		double ix = this.initXY.getX();
		double iy = this.initXY.getY();
		
		double nx = x*x - y*y + ix;
		double ny = 2*x*y + iy;
		
		return new Vector(nx, ny);
	}
	
	private Color getColor(double ang, double dist) {
		Color c;
		//c = new Color(1.0f, r.nextFloat()*0.2f + 0.6f, 0.6f);
		c = new Color(Color.HSBtoRGB((float)(ang/2.0/Math.PI), 1.0f, 1.0f));
		//c = new Color(Color.HSBtoRGB((float)(ang/2.0/Math.PI), 1.0f, (float)(1.0 - 1.0/(dist+1.0))));
		//c = new Color((float)(1.0-1.0/(dist*100.0+1.0)), 0.0f,(float)(1.0/(dist*100.0+1.0)));
//		if(dist < 4.0) {
//			c = Color.WHITE;
//		}
//		else {
//			c = new Color(0, 0, 0);
//		}
		//c = Color.WHITE;
		return c;
	}
	private float getAlpha(double ang, double dist, int i) {
		double alpha;
		if(dist > 0.0 && dist < 4.0) {
			alpha = i/(double)Main.DIVISIONS/8.0;
		}
		else {
			alpha = 0.0;
		}
		
		//alpha = 1.0;
		
		//alpha = 1.0 - 1.0/(dist+1.0);
		
		return (float)alpha;
	}
	
	private double t;
	private double life;
	private Vector[] xys = new Vector[Main.DIVISIONS+1];
	private Vector[] pastxys = new Vector[Main.DIVISIONS+1];
	private Vector initXY;
	
	//Counts the division number from Main
	private int cycleCount = 0;
	//Cycle number in which it resets in between frames
	//-1 if there's none
	private int breakPoint = -1;
	
	//Stores the number to run all the points
	private static int pointNum = -Main.POINTS;
	private static Random r = new Random();
	public Point() {
		super(new Vector(0,0), new Vector(0,0));
		//Reset
		this.reset();
		//Start at a random t(in the range [timeMin, timeMax]) or timeMin (to synchronize)
		if(randomTime) {
			this.t = timeMin + r.nextDouble()*(timeMax-timeMin);
		}
		else {
			this.t = timeMin;
		}
		//Start at 0 or a random point in life
		if(allPoints) {
			this.life = 0.0;
		}
		else {
			this.life = r.nextDouble()*lifeSpan;
		}
		//Fill all the points
		for(int i = 0; i <= Main.DIVISIONS; i++) {
			this.xys[i] = this.getXY();
			this.pastxys[i] = this.getXY();
		}
	}
	public void reset() {
		//System.out.println("Reset");
		//Set to the next point on the screen or a random one
		Vector newXY;
		if(allPoints) {
			newXY = new Vector(pointNum%Main.WIDTH,Math.floor(pointNum/(double)Main.WIDTH));
			pointNum += 1;

			//	System.out.println(applyTransformation(newXY));
		}
		else {
			newXY = new Vector(r.nextDouble(), r.nextDouble()).mult(Main.SIZE);
		}
		//Do reverse transformations to get the point
		newXY.subToThis(Screen.translate);
		newXY.divToThis(Screen.getScale());
		newXY.subToThis(Screen.getTranslate());
		this.setXY(newXY);
		//Reset time alive
		this.life = 0.0;
		//Set the first xy
		this.initXY = this.getXY();
		this.xys[this.cycleCount] = this.getXY();
		this.pastxys[this.cycleCount] = this.getXY();
		//Reset velocity (probably not needed)
		this.setV(new Vector(0.0,0.0));
	}
	//Translates and scales it to the screen
	private Vector applyTransformation(Vector xy) {
		return xy.add(Screen.getTranslate()).mult(Screen.getScale()).add(Screen.translate).round();
	}
	@Override
	public void draw(Graphics2D g2d) {
		//System.out.println("Draw");
		Graphics2D copy = (Graphics2D)g2d.create();
		//Set up the first point
		Vector xy1 = this.xys[0].clone();
		Vector xy2 = xy1.clone();
		
		//Only draw the initial point to the final point
		if(drawFinal) {
			xy1 = this.initXY.clone();
			xy2 = this.xys[Main.DIVISIONS].clone();
			
			Vector dxy = xy2.sub(xy1);
			double ang = dxy.getAng();
			double dist = dxy.getMagnitude();
			copy.setPaint(this.getColor(ang, dist));
			copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getAlpha(ang, dist, Main.DIVISIONS)));
			
			//Transform the points
			Vector txy1 = applyTransformation(xy1);
			Vector txy2 = applyTransformation(xy2);
			if(drawLine) {
				copy.drawLine((int)txy1.getX(), (int)txy1.getY(), (int)txy2.getX(), (int)txy2.getY());
			}
			else {
				copy.drawRect((int)txy1.getX(), (int)txy1.getY(), 0, 0);
			}
			return;
		}
		for(int i = 1; i <= Main.DIVISIONS; i++) {
			//Do not draw the line if the breakpoint is at this point
			if(i == (this.breakPoint)) {
				xy2 = this.xys[i].clone();
				this.breakPoint = -1;
				continue;
			}
			xy1 = xy2.clone();
			xy2 = this.xys[i].clone();
			
			Vector dxy = xy2.sub(xy1);
			double ang = dxy.getAng();
			double dist = dxy.getMagnitude();
			copy.setPaint(this.getColor(ang, dist));
			copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getAlpha(ang, dist, i)));
			
			//Transform the points
			Vector txy1 = applyTransformation(xy1);
			Vector txy2 = applyTransformation(xy2);
			if(drawLine) {
				copy.drawLine((int)txy1.getX(), (int)txy1.getY(), (int)txy2.getX(), (int)txy2.getY());
			}
			else {
				copy.drawRect((int)txy1.getX(), (int)txy1.getY(), 0, 0);
			}
			if(connectPast) {
				Vector xy3 = this.pastxys[i].clone();
				Vector txy3 = applyTransformation(xy3);
				copy.drawLine((int)txy1.getX(), (int)txy1.getY(), (int)txy3.getX(), (int)txy3.getY());
			}
		}
		copy.dispose();
	}

	@Override
	public void run(double dt) {
		//System.out.println("Calculate");
		//System.out.println(this.getXY());
		//Storing the past points for display
		//Set the first point to be the last one from the last frame at the start of the cycle
		//Also set the past xys to this
		//Reset breakpoint
		if(this.cycleCount == 0) {
			this.pastxys = this.xys.clone();
			this.xys[0] = this.xys[Main.DIVISIONS].clone();
		}
		//Reset if time runs out
		if(reset) {
			if(lifeIterations) {
				//Adds 1 to life at the start of every cycle
				if(cycleCount == 0) {
					this.life += 1.0;
				}
			}
			else {
				//Add dt to time alive
				this.life += dt;
			}
			//If time alive is greater than the lifespan it resets
			if(this.life >= lifeSpan) {
				this.reset();
				this.breakPoint = this.cycleCount;
			}
		}
		//Calculations
		//Get calculation time scaled down/up
		double calcdt = dt*TIMESCALE;
		//Add time to running time of the point (can be synchronous or not with other points)
		this.t += calcdt;
		//Have time in a range (might change)
		if(this.t > timeMax) {
			this.t = timeMin;
		}
		//Calculate the next point by the function which gives some value
		Vector func = this.calcNext();
		//Decide whenever or not the value from calcNext is the derivative or point coords
		if(derivative) {
			this.setV(func);
			this.move(calcdt);
		}
		else {
			this.setXY(func);
			//System.out.println("Current Point and Cycle");
			//System.out.println(this.getXY());
			//System.out.println(this.cycleCount);
		}
		//Save to past points (first point is allocated for the last point from the last frame)
		this.xys[this.cycleCount+1] = this.getXY();
		
		//Counts the subdivisions between each frame
		this.cycleCount += 1;
		this.cycleCount %= Main.DIVISIONS;
	}
	public static void resetDrawing() {
		pointNum = 0;
	}
}
