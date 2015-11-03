package servlet.admin;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import controller.AddBatchController;
import controller.BootstrapController;
import java.util.TreeMap;

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
        TreeMap<String, Integer> recordMap = null;
        String option = request.getParameter("option");
        Part filePart = request.getPart("zipFile");
		long timeBefore = System.currentTimeMillis();
        if(option.equals("bootstrap")){
            
            //BOOTSTRAP

             // Retrieves <input type="file" name="zipFile">
                if (filePart != null && filePart.getSize() > 0) {

                    //Create ERROR MAPS - and pass to boostrapController to generate
                    TreeMap<Integer, String> userErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> appErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> locErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> auErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> luErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> delErrMap = new TreeMap<Integer, String>();

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

        } else {
            
            //ADD BATCH
            try {
                if(filePart != null && filePart.getSize() > 0){
				     TreeMap<Integer, String> userErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> auErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> luErrMap = new TreeMap<Integer, String>();
                    TreeMap<Integer, String> delErrMap = new TreeMap<Integer, String>();

                    request.setAttribute("userErrMap", userErrMap);
                    request.setAttribute("auErrMap", auErrMap);
                    request.setAttribute("luErrMap", luErrMap);
                    request.setAttribute("delErrMap", delErrMap);

                    AddBatchController cntrl = new AddBatchController();
                    recordMap = cntrl.addBatch(filePart, userErrMap, delErrMap, auErrMap, luErrMap);
                }

            }catch (Exception e) {
                System.out.println("Exception Caught in bootstrap action.java");
                e.printStackTrace();
            }
        }
        
		long timeAfter = System.currentTimeMillis();
		
		long timeTaken = (timeAfter - timeBefore) / 1000;
		
		request.setAttribute("timeTaken", "" + timeTaken);
        request.setAttribute("recordMap", recordMap);
        System.out.println(recordMap);
        RequestDispatcher rd = request.getRequestDispatcher("admin");
        rd.forward(request, response);
        
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
