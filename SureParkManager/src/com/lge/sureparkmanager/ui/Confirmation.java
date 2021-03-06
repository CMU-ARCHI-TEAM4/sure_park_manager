package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lge.sureparkmanager.utils.Html;
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
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("userID");
		String confirmID = (String) session.getAttribute("confirmID");

		if (id == null || id == "" || confirmID == null || confirmID == "") {
			Log.d(TAG, "Session dose not exist");
			Html.executeJsGoBack(response);
			return;
		}
		
		Log.d(TAG, "userID " + id);
		Log.d(TAG, "confirmID " + confirmID);
		
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
		
		out.println("<tr ><td colspan=\"2\" align=\"center\"><h1>Thank you for Reservation! </br> Confirmation ID</h1></td></tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">ID</td>");
		out.println("<td><input type=\"text\" name=\"first_name\" value=" + confirmID +" "
				+ "readonly/></td>");
		
		
		String html="";
		html += "<tr ><td colspan='2' align='center'>";
        html += "<a href='reservation'><input type='button' value='More Reservation'/></a>";
        html += "<a href='logout'><input type='button' value='Logout'/></a>";
        html += "</td></tr></table></form>";
        
        out.println(html);
        
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
