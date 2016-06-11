package com.lge.sureparkmanager.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lge.sureparkmanager.manager.DataBaseManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    SystemManager mSystemManager;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("euc-kr");

		String id = request.getParameter("id");
		String pw = request.getParameter("passwd");
		
		request.setAttribute("id", "hello");

		HttpSession session = request.getSession();
		session.setAttribute("userID", id);
		
		Log.d(this.getClass().getName(), "userID " +  id);
		response.sendRedirect("reservation.html"); 
		
		
		
		
		//response.sendRedirect("SureParkManager/reservation.html"); 
//		DataBaseManager dbm = (DataBaseManager)SystemManager.getInstance().getManager(
//		        SystemManager.DATABASE_MANAGER);
//		if (dbm.getQueryWrapper().isLoginOk(id, pw)) {
//			HttpSession session = request.getSession();
//			session.setAttribute("userID", id);
//			response.sendRedirect("/reservation.html"); 
//		    //Html.writeHTML(response, "login success");
//		} else {
//		    Html.writeHTML(response, "login fail");
//		}

		//NetworkManager networkManager = (NetworkManager)SystemManager.getInstance().getManager(
        //        SystemManager.NETWORK_MANAGER);
		//networkManager.sendMessageToClients("This is server message");
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
