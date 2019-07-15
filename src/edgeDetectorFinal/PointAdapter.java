package edgeDetectorFinal;

import org.opencv.core.Point;

public class PointAdapter implements IPoint {

	Point p;

	public PointAdapter() {
		this.p = new Point();
	}

	public PointAdapter(double x, double y) {
		this.p = new Point(x, y);
	}

	@Override
	public double calcDist(IPoint o) {
		double res = Math.sqrt(Math.pow(o.getX() - this.getX(), 2)
				+ Math.pow(o.getY() - this.getY(), 2));
		return res;
	}

	@Override
	public double getX() {
		return p.x;
	}

	@Override
	public double getY() {
		return p.y;
	}
	
}
