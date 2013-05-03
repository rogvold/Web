/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cardio.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.cardio.core.managers.UserManagerLocal;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@WebServlet(name = "AvatarServlet", urlPatterns = {"/avatar"})
public class AvatarServlet extends HttpServlet {

    @EJB
    UserManagerLocal userMan;
    public static final String AVATAR_ID_NAME = "avatar_id";
    public static final String BASE = "/BaseProjectWeb";

    public static final String ANONYMOUS_IMAGE_URL = "http://png-1.findicons.com/files/icons/716/user_task_report/256/user_anonymous_yellow_disabled.png";
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
//            Long avId = Long.parseLong(null)
            Long userId = null;
            Long avId = null;
            String uId = request.getParameter(AVATAR_ID_NAME);
            if (uId != null) {
                userId = Long.parseLong(uId);
            }
            if (userId != null) {
                response.sendRedirect(getAvatarPath(userMan.getUserAvatar(userId)));
                return;
            }
            userId = (Long) SessionListener.getSessionAttribute(request.getSession(), "userId");
            if (userId != null) {
                response.sendRedirect(getAvatarPath(userMan.getUserAvatar(userId)));
                return;
            }
            response.sendRedirect(ANONYMOUS_IMAGE_URL);
        } finally {
            out.close();
        }
    }
    
    private String getAvatarPath(Long avatarId){
        if (avatarId == null){
            return ANONYMOUS_IMAGE_URL;
        }else{
            return BASE + "/download?id=" + avatarId;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
