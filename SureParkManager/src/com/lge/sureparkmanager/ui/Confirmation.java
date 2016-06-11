package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lge.sureparkmanager.utils.Log;

/**
 * Servlet implementation class Confirmation
 */
@WebServlet(description = "Show confirmation ID for drivers reservation", urlPatterns = { "/confirmation" })
public class Confirmation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String TAG = Confirmation.class.getSimpleName();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Confirmation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("userID");
		Log.d(TAG, "userID " + id);
		
		//TODO ; get confirmation ID from DB
		String confiramtionID = "201606111530_A0001";
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>");
		out.println("<html>");

		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=EUC-KR\">");
		out.println("<title>Sure-Park</title>");
		out.println("<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\">");
		out.println("</head>");

		out.println("<body>");
		out.println("<form action=\"confirmation\" method=\"post\">");
		out.println("<table class=\"centerTable\">");
		
		out.println("<tr ><td colspan=\"2\" align=\"center\"><h1>Confirmation ID of Your Reservation</h1></td></tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">ID</td>");
		out.println("<td><input type=\"text\" name=\"first_name\" value=" + confiramtionID +" "
				+ "readonly/></td>");
		out.println("</tr>");
		
		out.println("</table>");
		out.println("</form>");
		out.println("</body>");

		out.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
