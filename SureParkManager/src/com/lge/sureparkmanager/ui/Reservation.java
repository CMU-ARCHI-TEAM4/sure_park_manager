package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lge.sureparkmanager.db.UserInformation;
import com.lge.sureparkmanager.manager.DataBaseManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;

/**
 * Servlet implementation class Reservation
 */
@WebServlet(description = "reservation for drivers", urlPatterns = { "/reservation" })
public class Reservation extends HttpServlet {
	
	private static final String TAG = Reservation.class.getSimpleName();
	
	private String firstName = null;
	private String lastName = null;
	private String email = null;
	private String phoneNumeber = null;
	private String creditCardNumber = null;
	private String creditValidation = null;
	
	private String currentDate = null;
	private String maxDate = null; // 3 hours later
	private String currentTime = null;
	private String maxTime = null;
	
	private DataBaseManager dbm = (DataBaseManager)SystemManager.getInstance().getManager(
	        SystemManager.DATABASE_MANAGER);
	
			
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Reservation() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * get user information from DB
     * @param id
     */
    private void getDataFromDB(String id) {
    	
    	UserInformation ui = dbm.getQueryWrapper().getUserInfomation(id);
		
    	firstName = ui.getFristName();
    	lastName = ui.getLastName();
    	email = ui.getEmail();
    	phoneNumeber = ui.getPhoneNumber();
    	creditCardNumber = ui.getCreditCardNumber();
    	creditValidation = ui.getCreditCardValidation();
    }
    
    /**
     * make confirmation id
     * @param id
     * @param facility
     * @param startDate
     * @param startTime
     * @param endDate
     * @param endTime
     * @return
     */
    private String makeConfirmationID(String id, String facility, String startDate, String startTime, String endDate, String endTime){
    
    	final String start = startDate + " " + startTime;
    	final String end = endDate + " "+ endTime;

    	return dbm.getQueryWrapper().addReservation(id, facility, start, end);
    }
    
    /**
     * get list of facility
     * @return list
     */
    private List<String> getListOfFacility() {
    	return dbm.getQueryWrapper().getListOfFacility();
    }
    /**
     * get boundary date
     */
	private void getDateBoudary() {
		
		Date date = new Date();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		currentDate = dateFormat.format(date);
		Calendar c = Calendar.getInstance(); 
		c.setTime(date); 
		c.add(Calendar.HOUR_OF_DAY, 3);
		Date tomorrow = c.getTime();

		maxDate = dateFormat.format(tomorrow);
		
		dateFormat = new SimpleDateFormat("HH:mm");
		currentTime = dateFormat.format(date);
		
		maxTime = dateFormat.format(tomorrow);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("userID");
		Log.d(TAG, "userID " + id);
		
		session.removeAttribute("confirmID");
		
		if (id == null || id =="" ) {
			Log.d(TAG, "Session dose not exist");
			 Html.executeJsGoBack(response);
			 return;
		}
		
		getDateBoudary();
		getDataFromDB(id);
		List<String> facilities = getListOfFacility();
		
		//TEST CODE; remove it
		//List<String> facilities = new ArrayList<String>();
		//facilities.add("A");
		//facilities.add("B");
		//END TEST CODE
		
		//default facility
		String facility = facilities.get(0);
		
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
		out.println("<form action=\"reservation\" method=\"post\">");
		out.println("<table class=\"centerTable\">");
		out.println("<tr ><td colspan=\"2\" align=\"center\"><h1>Sure's Park Reservation System</h1></td></tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">First Name</td>");
		out.println("<td><input type=\"text\" name=\"first_name\" value=" + firstName +" "
				+ "readonly/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">Last Name</td>");
		out.println("<td><input type=\"text\" name=\"last_name\" value=" + lastName +" "
				+ "readonly/></td>");
		out.println("</tr>");
		
		final int size = facilities.size();
		if (size > 1) {
			out.println("<tr>");
			out.println("<td align=\"center\">Facility</td>");
			out.println("<td><select name=\"facility\">");
			for (int i=0; i<size; i++) {
				out.println("<option value=\"" + facilities.get(i)  + "\">" + facilities.get(i) + "</option>");
			}
			out.println("<td></select>");
		}
		
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td align=\"center\">Start Date </td>");
		out.println("<td><input type=\"date\" name=\"start_date\" " +  "value=" + currentDate + " max=" + maxDate + " " +  "min=" + currentDate +" "
				+ "/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">Start Time</td>");
		out.println("<td><input type=\"time\" name=\"start_time\" " + "value="+ currentTime + " max="+ maxTime  +" "
				+ "/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">End Date</td>");
		out.println("<td><input type=\"date\" name=\"end_date\" + " + "value="+ currentDate + " " + "min=" + maxDate +" "
				+ "/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">End Time</td>");
		out.println("<td><input type=\"time\" name=\"end_time\" "  + "value="+ currentTime + " "
				+ "/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">E-mail</td>");
		out.println("<td><input type=\"text\" name=\"email\" value=" + email +" "
				+ "readonly/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">Phone Number</td>");
		out.println("<td><input type=\"text\" name=\"phoneNumber\" value=" + phoneNumeber + " "
				+ "readonly/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">Credit Card Number</td>");
		out.println("<td><input type=\"text\" name=\"card_number\" value=" + creditCardNumber + " "
				+ "readonly/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td align=\"center\">Credit Card Validation Date</td>");
		out.println("<td><input type=\"text\" name=\"card_date\" value="+ creditValidation + " "
				+ "readonly/></td>");
		out.println("</tr>");
		                
		String html="";
		html += "<tr ><td colspan='2' align='center'>";
        html += "<input type='submit' value='Reservation'/></a>";
        html += "<a href='logout'><input type='button' value='Cancle'/></a>";
        html += "</td></tr></table></form>";
        
        out.println(html);
        
		//out.println("<tr ><td colspan=\"2\" align=\"center\"><input type=\"submit\" value=\"Reservation\"/></td></tr>");
		//out.println("<a href='javascript:window.history.back();'><input type='button' value='Cancle'/></a>");
		out.println("</table>");
		out.println("</form>");
		out.println("</body>");

		out.println("</html>");
		
		final String startDate = request.getParameter("start_date");
		final String startTime = request.getParameter("start_time");
		final String endDate = request.getParameter("end_date");
		final String endTime = request.getParameter("end_time");
		facility = request.getParameter("facility");

		Log.d(TAG, "startDate " + startDate);
		Log.d(TAG, "startTime " + startTime);
		Log.d(TAG, "endDate " + endDate);
		Log.d(TAG, "endTime " + endTime);
		Log.d(TAG, "facility " + facility);
		
		// redirect to confirmation page
		if (endTime != null && endDate != null) {
			final String confirmID = makeConfirmationID(id, facility, startDate, startTime, endDate, endTime);
			if (confirmID == null) {
				// TODO; failed to reserve
				response.sendRedirect("welcome.html");
				out.close();
				return;
			}
			session.setAttribute("confirmID", confirmID);
			response.sendRedirect("confirmation"); 
		}
		
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doGet(request, response);
		
		
	}

}
