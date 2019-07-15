package edgeDetectorFinal;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.opencv.core.*;

public interface IImageProc {
	
	public static final double LIMIT_WIDTH = 250;

	public Mat resizeImage(Mat m, int wLimit);

	public IPoint[] getEdge(byte[] imageData, BufferedImage bi) throws IOException;
}
