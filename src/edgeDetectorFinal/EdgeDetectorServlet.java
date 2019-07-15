package edgeDetectorFinal;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class EdgeDetectorServlet
 */
@WebServlet("/EdgeDetectorServlet")
@MultipartConfig
public class EdgeDetectorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EdgeDetectorServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		JsonObject jsonObject = null;

		try {
			Part filePart = request.getPart("file");
			String contentType = filePart.getContentType();
			if (contentType.equals("image/jpeg")
					|| contentType.equals("image/png")) {
				IPoint[] points = getPoints(filePart);
				jsonObject = sendJson(response, points);
			} else {
				response.setStatus(500);
				jsonObject = sendError(response,
						"Image type must be JPEG or PNG");
			}
			response.getWriter().append(jsonObject.toString());
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JsonObject sendError(HttpServletResponse response, String mess) {
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		objectBuilder.add("Result", 500).add("message", mess != null ? mess
				: "The server failed to fulfill the request");
		JsonObject jsonObject = objectBuilder.build();
		return jsonObject;
	}

	/**
	 * @param response
	 * @param points
	 * @throws IOException
	 */
	private JsonObject sendJson(HttpServletResponse response, IPoint[] points)
			throws IOException {
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		objectBuilder.add("Result", 200);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (IPoint p : points) {
			arrayBuilder.add(Json.createObjectBuilder().add("x", (int) p.getX())
					.add("y", (int) p.getY()).build());
		}
		objectBuilder.add("message", arrayBuilder);
		JsonObject jsonObject = objectBuilder.build();
		return jsonObject;
	}

	/**
	 * @param filePart
	 * @return
	 * @throws IOException
	 */
	private IPoint[] getPoints(Part filePart) throws IOException {
		InputStream is = filePart.getInputStream();
		BufferedImage bi = ImageIO.read(filePart.getInputStream());
		Client client = new Client();
		IPoint[] points = client.getPath(is, bi);
		return points;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
