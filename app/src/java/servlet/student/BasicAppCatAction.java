/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.BasicAppController;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zhihui
 */
@WebServlet(name = "BasicAppCatAction", urlPatterns = {"/BasicAppCatAction"})
public class BasicAppCatAction extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String sdate = request.getParameter("startdate");
            String edate = request.getParameter("enddate");

            //VALIDATION
            ArrayList<String> errors = new ArrayList<String>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = sdf.parse(sdate + " " + "00:00:00", new ParsePosition(0));
            if (startDate == null) {
                errors.add("You have entered an invalid start date!");
            }
            Date endDate = sdf.parse(edate + " " + "23:59:59", new ParsePosition(0));
            if (endDate == null) {
                errors.add("You have entered an invalid end date!");
            }

            BasicAppController bacCtrl = new BasicAppController();
            
            //IF FAIL VALIDATION, SEND ERROR MESSAGE
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                RequestDispatcher rd = request.getRequestDispatcher("basicapp-appcat.jsp");
                rd.forward(request, response);
                return;
            } else {
                TreeMap<String, Double[]> catList = bacCtrl.generateAppCategory(startDate, endDate);
                request.setAttribute("result", catList);
                RequestDispatcher rd = request.getRequestDispatcher("basicapp-appcat.jsp");
                rd.forward(request,response);
                return;
            }
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
