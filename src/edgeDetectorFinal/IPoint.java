package edgeDetectorFinal;

import java.util.Iterator;
import java.util.List;

import org.opencv.core.Point;

public interface IPoint {

	public double calcDist(IPoint o);
	public double getX();
	public double getY();
	
	public static IPoint[] convertToIPointList(List<Point> list) {
		IPoint[] res = new PointAdapter[list.size()];
		Iterator<Point> i = list.iterator();
		int count = 0;
		while(i.hasNext()) {
			Point p = i.next();
			res[count] = new PointAdapter(p.x, p.y);
			count++;
		}
		return res;
	}

}
