package edgeDetectorFinal;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client {

	private IImageProc imgAdapter;

	// Main for testing
	// public static void main(String[] args) throws IOException {
	//
	// File f = new File(
	// "C:\\Users\\Simone\\eclipse-workspace\\edgeDetector\\images\\heart.jpg");
	// InputStream in = new FileInputStream(f);
	// BufferedImage bi = ImageIO.read(f);
	// System.out.println(bi.getType());
	//
	// Client c = new Client();
	// IPoint[] points = c.getPath(in, bi);
	// System.out.println("Length: " + points.length);
	// for (int i = 0; i < points.length; i++) {
	// System.out.format("x: %f, y: %f%n", points[i].getX(), points[i].getY());
	// }
	// }

	public Client() {
		this.imgAdapter = new ImageProcAdapter();
	}

	public IPoint[] getPath(InputStream inputImage, BufferedImage bi) {

		byte[] byteArray;
		try {
			byteArray = org.apache.commons.io.IOUtils.toByteArray(inputImage);
		} catch (IOException e1) {
			return null;
		}

		IPoint[] coords = null;
		try {
			coords = imgAdapter.getEdge(byteArray, bi);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (coords.length > 5000) {
			coords = reduceElements(coords, 10);
		} else if (coords.length > 100) {
			coords = reduceElements(coords, 5);
		}
		if (coords.length > 0) {
			coords = getTour(coords);
		}

		return coords;
	}

	/**
	 * Riduce il numero di elementi di un array
	 * 
	 * @param coords
	 *            Array da ridurre
	 * @param step
	 *            Numero di elementi che vengono saltati ad ogni iterazione
	 * @return
	 */
	private static IPoint[] reduceElements(IPoint[] coords, int step) {
		List<IPoint> res = new ArrayList<IPoint>();
		for (int i = 0; i < coords.length; i += step) {
			res.add(coords[i]);
		}
		return res.toArray(new IPoint[0]);
	}

	private static IPoint[] getTour(IPoint[] coords) {
		List<Integer> order = new ArrayList<Integer>(coords.length);
		List<IPoint> resArr = new ArrayList<IPoint>();
		// Start from random point
		Random r = new Random(System.currentTimeMillis());
		int initIndex = r.nextInt(coords.length);
		resArr.add(coords[initIndex]);
		for (int i = 0; i < coords.length; i++) {
			if (i != initIndex) {
				order.add(i);
			}
		}
		while (order.size() > 0) {
			double minDist = Double.MAX_VALUE;
			int k = order.get(0);
			int j = 0;
			for (int i = 0; i < order.size(); i++) {
				double d = (resArr.get(resArr.size() - 1))
						.calcDist(coords[order.get(i)]);
				if (d < minDist && d > 0) {
					minDist = d;
					k = order.get(i);
					j = i;
				}
			}
			resArr.add(coords[k]);
			order.remove(j);
		}
		return resArr.toArray(new IPoint[0]);
	}

}
