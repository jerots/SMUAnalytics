package servlet;

import java.io.*;
import java.sql.SQLException;
import java.util.zip.ZipInputStream;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import dao.*;
import com.opencsv.CSVReader;
import controller.BootstrapController;
import java.util.HashMap;
import java.util.zip.ZipEntry;

@WebServlet(urlPatterns = {"/BootstrapAction"})
@MultipartConfig

public class BootstrapAction extends HttpServlet {

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

            HashMap<String, Integer> recordMap = null;

            Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
            if(filePart != null && filePart.getSize() > 0){

                //Create ERROR MAPS - and pass to boostrapController to generate
                HashMap<Integer, String> userErrMap = new HashMap<>();
                HashMap<Integer, String> appErrMap = new HashMap<>();
                HashMap<Integer, String> locErrMap = new HashMap<>();
                HashMap<Integer, String> auErrMap = new HashMap<>();
                HashMap<Integer, String> luErrMap = new HashMap<>();
                HashMap<Integer, String> delErrMap = new HashMap<>();
                
                //SET FOR UI TO GET THE ATTRIBUTES.
                request.setAttribute("userErrMap", userErrMap);
                request.setAttribute("appErrMap", appErrMap);
                request.setAttribute("locErrMap", locErrMap);
                request.setAttribute("auErrMap", auErrMap);
                request.setAttribute("luErrMap", luErrMap);
                request.setAttribute("delErrMap", delErrMap);

                BootstrapController ctrl = new BootstrapController();
                
                recordMap = ctrl.bootstrap(filePart, userErrMap, appErrMap, locErrMap, auErrMap, luErrMap, delErrMap);
            }
            request.setAttribute("recordMap", recordMap);

            RequestDispatcher rd = request.getRequestDispatcher("/admin/home.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            System.out.println("Exception Caught in bootstrap action.java");
            e.printStackTrace();
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
