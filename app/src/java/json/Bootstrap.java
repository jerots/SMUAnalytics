/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import controller.BootstrapController;
import dao.InitDAO;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.google.gson.JsonArray;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "Bootstrap", urlPatterns = {"/json/bootstrap"})
public class Bootstrap extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		try (PrintWriter out = response.getWriter()) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject output = new JsonObject();
			JsonArray errors = new JsonArray();

			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			InputStream fis = null;

			try {
				FileItemIterator iter = upload.getItemIterator(request);
				boolean hasFile = false;
				while (iter.hasNext()) {

					FileItemStream item = iter.next();
					if (item.isFormField() && item.getFieldName().equals("token")) {
						//IF IT IS A TOKEN, VALIDATE
						Scanner sc = new Scanner(item.openStream());
						if (sc.hasNext()) {
							String token = sc.next();

							//TOKEN VALIDATION
							if (token == null) {
								errors.add("missing token");
							} else if (token.length() == 0) {
								errors.add("blank token");
							} else {
								try {
									String username = JWTUtility.verify(token, "nabjemzhdarrensw");
									if (username == null) {
										//failed
										errors.add("invalid token");
									}

								} catch (JWTException e) {
									//failed
									errors.add("invalid token");
								}
							}
						}
					}
					//IF bootstrap-file field is found
					if (!item.isFormField() && item.getFieldName().equals("bootstrap-file")) {
						hasFile = true;

						//If not zip file
						if (!item.getContentType().equals("application/zip")) {
							errors.add("invalid file");
						} else {
							//If zip file
							fis = item.openStream();
						}
					}
				}

				//IF bootstrap-file field not found
				if (!hasFile) {
					errors.add("missing file");
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}

			//PRINT ERROR AND EXIT IF ERRORS EXIST
			if (errors.size() > 0) {
				output.addProperty("status", "error");
				output.add("errors", errors);
				out.println(gson.toJson(output));
				return;
			}

			//IF PASS VALIDATION, READ FILES IN BOOTSTRAP
			Part filePart = null;
			
			//Create ERROR MAPS - and pass to boostrapController to generate
			HashMap<Integer, String> userErrMap = new HashMap<Integer, String>();
			HashMap<Integer, String> appErrMap = new HashMap<Integer, String>();
			HashMap<Integer, String> locErrMap = new HashMap<Integer, String>();
			HashMap<Integer, String> auErrMap = new HashMap<Integer, String>();
			HashMap<Integer, String> luErrMap = new HashMap<Integer, String>();
			HashMap<Integer, String> delErrMap = new HashMap<Integer, String>();

			HashMap<String, Integer> recordMap = null;

			try {
				BootstrapController ctrl = new BootstrapController();
				recordMap = ctrl.bootstrap(filePart, userErrMap, appErrMap, locErrMap, auErrMap, luErrMap, delErrMap);
				//Returns success as the head of the JSON if it is a success.
				boolean success = false;
				if (userErrMap.isEmpty() && appErrMap.isEmpty() && locErrMap.isEmpty() && auErrMap.isEmpty() && luErrMap.isEmpty() && delErrMap.isEmpty()) {
					success = true;
					output.addProperty("status", "success");
				} else {
					output.addProperty("status", "error");
				}

				//Iterates through the main HashMap to consolidate the number of rows that were updated.
				Iterator<String> iter = recordMap.keySet().iterator();
				JsonArray arr = new JsonArray();
				JsonObject list = new JsonObject();
				while (iter.hasNext()) {
					String fileName = iter.next();
					list.addProperty(fileName, recordMap.get(fileName));
					arr.add(list);
				}
				output.add("num-record-uploaded", arr);

				if (!success) { //This only occurs when there is an error
					//Iterates through to find the unique row numbers that are affected. This is for AppDAO or app-lookup.csv
					Iterator<Integer> iterInt = appErrMap.keySet().iterator();
					arr = new JsonArray();
					list = new JsonObject();
					JsonArray errorArr = new JsonArray();
					//Goes through the list to split all the error messages into a jsonarray
					while (iterInt.hasNext()) {
						int id = iterInt.next();
						list.addProperty("file", "app-lookup.csv");
						list.addProperty("line", id);
						String[] messages = appErrMap.get(id).split(",");
						for (String msg : messages) {
							errorArr.add(msg);
						}
						list.add("message", arr);
					}
					arr.add(list);

					//Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
					iterInt = userErrMap.keySet().iterator();
					arr = new JsonArray();
					list = new JsonObject();
					errorArr = new JsonArray();
					//Goes through the list to split all the error messages into a jsonarray
					while (iterInt.hasNext()) {
						int id = iterInt.next();
						list.addProperty("file", "demographics.csv");
						list.addProperty("line", id);
						String[] messages = userErrMap.get(id).split(",");
						for (String msg : messages) {
							errorArr.add(msg);
						}
						list.add("message", arr);
					}
					arr.add(list);

					//Iterates through to find the unique row numbers that are affected. This is for App-lookup.csv/AppUsageDAO
					iterInt = auErrMap.keySet().iterator();
					arr = new JsonArray();
					list = new JsonObject();
					errorArr = new JsonArray();
					//Goes through the list to split all the error messages into a jsonarray
					while (iterInt.hasNext()) {
						int id = iterInt.next();
						list.addProperty("file", "app.csv");
						list.addProperty("line", id);
						String[] messages = auErrMap.get(id).split(",");
						for (String msg : messages) {
							errorArr.add(msg);
						}
						list.add("message", arr);
					}
					arr.add(list);

					//Iterates through to find the unique row numbers that are affected. This is for LocationDAO/demographics.csv
					iterInt = locErrMap.keySet().iterator();
					arr = new JsonArray();
					list = new JsonObject();
					errorArr = new JsonArray();
					//Goes through the list to split all the error messages into a jsonarray
					while (iterInt.hasNext()) {
						int id = iterInt.next();
						list.addProperty("file", "location-lookup.csv");
						list.addProperty("line", id);
						String[] messages = locErrMap.get(id).split(",");
						for (String msg : messages) {
							errorArr.add(msg);
						}
						list.add("message", arr);
					}
					arr.add(list);

					//Iterates through to find the unique row numbers that are affected. This is for LocationUsageDAO/demographics.csv
					iterInt = luErrMap.keySet().iterator();
					arr = new JsonArray();
					list = new JsonObject();
					errorArr = new JsonArray();
					//Goes through the list to split all the error messages into a jsonarray
					while (iterInt.hasNext()) {
						int id = iterInt.next();
						list.addProperty("file", "location.csv");
						list.addProperty("line", id);
						String[] messages = luErrMap.get(id).split(",");
						for (String msg : messages) {
							errorArr.add(msg);
						}
						list.add("message", arr);
					}
					arr.add(list);

					//Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
					iterInt = delErrMap.keySet().iterator();
					arr = new JsonArray();
					list = new JsonObject();
					errorArr = new JsonArray();
					//Goes through the list to split all the error messages into a jsonarray
					while (iterInt.hasNext()) {
						int id = iterInt.next();
						list.addProperty("file", "location-delete.csv");
						list.addProperty("line", id);
						String[] messages = delErrMap.get(id).split(",");
						for (String msg : messages) {
							errorArr.add(msg);
						}
						list.add("message", arr);
					}
					arr.add(list);

					output.add("error", arr);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			out.println(gson.toJson(output));
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
