package com.lge.sureparkmanager.ui;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lge.sureparkmanager.manager.LogManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.WebSession;

/**
 * Servlet implementation class Welcome
 */
@WebServlet(description = "logout", urlPatterns = { "/logout" })
public class Logout extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private LogManager lm = (LogManager) SystemManager.getInstance()
            .getManager(SystemManager.LOG_MANAGER);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute(WebSession.SESSION_USERID);

        final String sessionUserId = (String) request.getSession()
                .getAttribute(WebSession.SESSION_USERID);

        if ("attendant".equals(sessionUserId)) {
            lm.log(LogManager.ATTENDANT, LogManager.LOGOUT);
        } else if ("admin".equals(sessionUserId)) {
            lm.log(LogManager.OWNER, LogManager.LOGOUT);
        } else {
            lm.log(LogManager.DRIVER, LogManager.LOGOUT + "/" + sessionUserId);
        }

        response.sendRedirect("welcome.html");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
