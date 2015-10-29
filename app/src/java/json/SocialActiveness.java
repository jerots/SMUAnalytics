///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package json;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import controller.SocialActivenessController;
//import dao.UserDAO;
//import dao.Utility;
//import entity.User;
//import is203.JWTException;
//import is203.JWTUtility;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.ParsePosition;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.TreeMap;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// *
// * @author jeremyongts92
// */
//@WebServlet(name = "SocialActiveness", urlPatterns = {"/json/social-activeness-report"})
//public class SocialActiveness extends HttpServlet {
//
//	/**
////	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
//	 * methods.
//	 *
//	 * @param request servlet request
//	 * @param response servlet response
//	 * @throws ServletException if a servlet-specific error occurs
//	 * @throws IOException if an I/O error occurs
//	 */
//	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		response.setContentType("application/json");
//		try (PrintWriter out = response.getWriter()) {
//
//			Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//			JsonObject output = new JsonObject();
//
//			JsonArray errors = new JsonArray();
//
//			String token = request.getParameter("token");
//			String date = request.getParameter("date");
//			String username = "";
//			//TOKEN VALIDATION
//			if (token == null) {
//				errors.add("missing token");
//			} else if (token.length() == 0) {
//				errors.add("blank token");
//			} else {
//				try {
//					username = JWTUtility.verify(token, "nabjemzhdarrensw");
//					if (username == null) {
//						//failed
//						errors.add("invalid token");
//					} else {
//						UserDAO userDAO = new UserDAO();
//						User user = userDAO.retrieve(username);
//						if (user == null) {
//							errors.add("invalid token");
//						}
//					}
//
//				} catch (JWTException e) {
//					//failed
//					errors.add("invalid token");
//				}
//
//			}
//
//			//START DATE VALIDATION
//			if (date == null) {
//				errors.add("missing startdate");
//			} else if (date.length() == 0) {
//				errors.add("blank startdate");
//			} else {
//				if (date.length() != 10) {
//					errors.add("invalid startdate");
//				} else {
//					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
//					Date dateFormatted = sdf.parse(date, new ParsePosition(0));
//					if (dateFormatted == null || !Utility.checkDate(date)) {
//						errors.add("invalid startdate");
//					}
//				}
//			}
//
//			//PRINT ERROR AND EXIT IF ERRORS EXIST
//			if (errors.size() > 0) {
//				output.addProperty("status", "error");
//				output.add("errors", errors);
//				out.println(gson.toJson(output));
//				return;
//			}
//
//			//PASSES ALL VALIDATION, proceed to onlineReport generation
//			output.addProperty("status", "success");
//
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date startDate = dateFormat.parse(date + " " + "00:00:00", new ParsePosition(0));
//			Date endDate = dateFormat.parse(date + " " + "23:59:59", new ParsePosition(0));
//
//			SocialActivenessController ctrl = new SocialActivenessController();
//
//			TreeMap<String, String> onlineReport = ctrl.generateOnlineReport(startDate, endDate, username);
//
//			JsonObject results = new JsonObject();
//			output.add("results", results);
//
//			String socialAppDuration = onlineReport.get("total-social-app-usage-duration");
//			results.addProperty("total-social-app-usage-duration", socialAppDuration);
//			
//			JsonArray appUsageArr = new JsonArray();
//			results.add("individual-social-app-usage", appUsageArr);
//			onlineReport.remove("total-social-app-usage-duration");
//			Iterator<String> iter = onlineReport.keySet().iterator();
//
//			while (iter.hasNext()) {
//				String appName = iter.next();
//				String percentage = onlineReport.get(appName);
//				JsonObject obj = new JsonObject();
//				obj.addProperty("app-name", appName);
//				obj.addProperty("percent", percentage);
//				appUsageArr.add(obj);
//
//			}
//			
//			HashMap<String,String> physicalReport = ctrl.generatePhysicalReport(startDate, endDate, username);
//			
//			String totalTime = physicalReport.get("total-time-spent-in-sis");
//			results.addProperty("total-time-spent-in-sis", totalTime);
//			
//			
//			out.println(gson.toJson(output));
//
//		}
//	}
//
//	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//	/**
//	 * Handles the HTTP <code>GET</code> method.
//	 *
//	 * @param request servlet request
//	 * @param response servlet response
//	 * @throws ServletException if a servlet-specific error occurs
//	 * @throws IOException if an I/O error occurs
//	 */
//	@Override
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		processRequest(request, response);
//	}
//
//	/**
//	 * Handles the HTTP <code>POST</code> method.
//	 *
//	 * @param request servlet request
//	 * @param response servlet response
//	 * @throws ServletException if a servlet-specific error occurs
//	 * @throws IOException if an I/O error occurs
//	 */
//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		processRequest(request, response);
//	}
//
//	/**
//	 * Returns a short description of the servlet.
//	 *
//	 * @return a String containing servlet description
//	 */
//	@Override
//	public String getServletInfo() {
//		return "Short description";
//	}// </editor-fold>
//
//}
