package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author katharina
 */
@WebServlet(name = "TaxCalcServlet", urlPatterns = {"/TaxCalcServlet"})
public class TaxCalcServlet extends HttpServlet {

    @Resource(mappedName = "java:jboss/exported/jms/queue/taxCalc")
    private Queue taxCalc;

    @Inject
    @JMSConnectionFactory("java:jboss/exported/jms/RemoteConnectionFactory")
    private JMSContext context;



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
            float amount = Float.parseFloat(request.getParameter("amount"));
            float tax = Float.parseFloat(request.getParameter("tax"));
            String currency = request.getParameter("currency");

            double buf = amount * ((tax + 100) / 100);
            // round 2 digits
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            double result = Double.valueOf(twoDForm.format(buf));
            String time = new Timestamp(System.nanoTime()).toString();
            
            String record = "Time: "+time+" amount: "+amount+" tax:"+tax+" total: "+result+" currency: "+currency;
            sendMessage(record);
            
            out.println("Total amount: " + result + " " + currency);

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

    private void sendMessage(String messageData) {
        System.out.println("Try to send message");
        
        
        context.createProducer().send(taxCalc, messageData);
    }


}
