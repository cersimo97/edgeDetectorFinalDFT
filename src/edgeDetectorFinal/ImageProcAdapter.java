package edgeDetectorFinal;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageProcAdapter implements IImageProc {

	static {
		// System.setProperty("java.library.path", "./libs");
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public ImageProcAdapter() {

	}

	@Override
	public Mat resizeImage(Mat m, int wLimit) {
		Size size = new Size(m.cols(), m.rows());
		if (size.width > wLimit || size.height > LIMIT_WIDTH) {
			Mat dst = new Mat();
			double factorScale = size.height / size.width;
			Size newSize = new Size(LIMIT_WIDTH,
					(int) (LIMIT_WIDTH * factorScale));
			Imgproc.resize(m, dst, newSize, 0, 0, Imgproc.INTER_AREA);
			return dst;
		}
		return m;
	}

	@Override
	public IPoint[] getEdge(byte[] imageData, BufferedImage bi)
			throws IOException {
		// System.out.println(bi.getType());
		Mat m = Imgcodecs.imdecode(new MatOfByte(imageData),
				Imgcodecs.IMREAD_UNCHANGED);

		// printImage(mat2Img(m), "");

		m = resizeImage(m, 250);
		// Imgcodecs.imwrite("result.jpg", m);
		// printImage(mat2Img(m), "test bi -> mat");

		Mat edges = new Mat();
		Imgproc.Canny(m, edges, 100, 60 * 4);

		// printImage(mat2Img(edges), "edge");

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_NONE);
		// Mat contourImg = new Mat(edges.size(), edges.type());
		// for (int i = 0; i < contours.size(); i++) {
		// Imgproc.drawContours(contourImg, contours, i, new Scalar(255, 255,
		// 255), -1);
		// }
		List<Point> pList = new ArrayList<Point>();
		for (int i = 0; i < contours.size(); i++) {
			pList.addAll(contours.get(i).toList());
		}

		return IPoint.convertToIPointList(pList);
	}

	private static void printImage(BufferedImage image, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		frame.pack();
		frame.setVisible(true);
	}

	private static BufferedImage mat2Img(Mat matrix) throws IOException {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage bi = ImageIO.read(in);
		return bi;
	}

}
