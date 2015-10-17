/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.BasicAppController;
import entity.Breakdown;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
@WebServlet(name = "BasicAppDiurnalAction", urlPatterns = {"/BasicAppDiurnalAction"})
public class BasicAppDiurnalAction extends HttpServlet {

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

            //RETRIEVING PARAMETERS
            String date = request.getParameter("startdate");
            String yearfilter = request.getParameter("yearfilter");
            String genderfilter = request.getParameter("genderfilter");
            String schoolfilter = request.getParameter("schoolfilter");

            //VALIDATION
            ArrayList<String> errors = new ArrayList<String>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = dateFormat.parse(date + " " + "00:00:00", new ParsePosition(0));

            if (startDate == null) {
                errors.add("You have entered an invalid date!");
            }

            String[] demoArr = new String[3];
            if (yearfilter != null && yearfilter.length() > 0) {
                demoArr[0] = yearfilter;
            }

            if (genderfilter != null && genderfilter.length() > 0) {
                demoArr[1] = genderfilter;
            }

            if (schoolfilter != null && schoolfilter.length() > 0) {
                demoArr[2] = schoolfilter;
            }

            //IF FAIL VALIDATION, SEND ERROR MESSAGE
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                RequestDispatcher rd = request.getRequestDispatcher("basicapp-diurnal.jsp");
                rd.forward(request, response);
                return;
            }

            BasicAppController ctrl = new BasicAppController();
            Breakdown breakdown = null;
            try {
                breakdown = ctrl.generateDiurnalReport(startDate, demoArr);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<HashMap<String, Breakdown>> breakdownList = breakdown.getBreakdown();
            request.setAttribute("result", breakdownList);
            RequestDispatcher rd = request.getRequestDispatcher("basicapp-diurnal.jsp");
            rd.forward(request, response);
            return;
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
