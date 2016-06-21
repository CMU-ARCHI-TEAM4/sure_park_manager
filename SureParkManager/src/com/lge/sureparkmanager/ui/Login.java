package com.lge.sureparkmanager.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import javax.imageio.ImageIO;
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
import com.lge.sureparkmanager.utils.WebSession;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;

@WebServlet({"/ad_login","/login"})
public class Login extends HttpServlet {
	private static final String TAG = Login.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	private String captchaAnswer = "";
	private final static boolean USE_CAPTCHA = false;
	
	public Login() {
		super();
		Log.d(TAG, "init");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		final StringBuffer requestUrl = request.getRequestURL();

		request.setCharacterEncoding("euc-kr");
		DataBaseManager dbm = (DataBaseManager) SystemManager.getInstance().getManager(SystemManager.DATABASE_MANAGER);

		String id = request.getParameter("id");
		String pw = request.getParameter("passwd");
		String captchaUser = request.getParameter("captcha");
		
		if (captchaAnswer.equals(captchaUser)) {
			Log.d(TAG, "Equals Confirmed with captcha " + captchaAnswer);
		}	
		
		if (dbm != null && dbm.getQueryWrapper().isLoginOk(id, pw) && (USE_CAPTCHA == false || captchaAnswer.equals(captchaUser))) {
			
			Log.d(TAG, "Athorized user " + captchaAnswer);
			HttpSession session = request.getSession();
			session.setAttribute(WebSession.SESSION_USERID, id);
			if ("attendant".equals(id)) {
				response.sendRedirect("pf");
			} else if ("admin".equals(id)) {
				response.sendRedirect("statistics");
			} else {
				session.setMaxInactiveInterval(WebSession.SESSION_TIMEOUT);
				response.sendRedirect("reservation");
			}
		} else {
			PrintWriter printWriter = null;
			try {
				printWriter = response.getWriter();
				printWriter.write(Html.getHtmlHeader());
				printWriter.write(getJsInputCheckSubmit("action_login"));
		
				if (requestUrl.toString().contains("ad_login")) {
					printWriter.write(getLoginHtml("Login for Administrator"));
				} else {
					printWriter.write(getLoginHtml("Login for Driver"));
				}
				
				printWriter.write(Html.getHtmlFooter());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (printWriter != null) {
					printWriter.close();
				}
			}
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String makeCaptcha() {
		
		Captcha captcha = new Captcha.Builder(120, 50)
				 .addText() 
				 .addBackground(new GradiatedBackgroundProducer())
				 .addNoise()
				 //.gimp(new DropShadowGimpyRenderer())
				 .addBorder()
				 .build();
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(captcha.getImage(), "PNG", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = out.toByteArray();

		byte[] base64bytes = Base64.getEncoder().encode(bytes);
		String src = "data:image/png;base64," + new String(base64bytes);

		captchaAnswer = captcha.getAnswer();
		return src;
	}

	private String getLoginHtml(String title) {
		
		final String captcha = makeCaptcha();
		String html = "";
		String action = "";
		String image = "";
		
		if (title.contains("Administrator")) {
			action = "<form action='ad_login' name='action_login' method='post'>";
			image = "<img src='images/lattanze.jpg' />";
        } else {
        	action = "<form action='login' name='action_login' method='post'>";
			image = "<img src='images/hulk_icon.png' width='400' height='200' />";
        }
		
		html += action;
        html += "<table class='centerTable' width='500'>";
        html += "<tr ><td colspan='2' align='center'>";
        html += image;
        html += "</td></tr><tr><td align='center'>ID</td>";
        html += "<td><input type='text' name='id' id='id' size='20' /></td>";
        html += "</tr><tr><td align='center'>PW</td>";
        html += "<td><input type='password' name='passwd' id='passwd' size='20' /></td></tr>";
		html += "<tr><td><img alt='lg twins' src='"+ captcha +"' /></td>";
		html += "<td><input type='text' name='captcha' id='captcha' /></tr>";
		html += "<tr ><td colspan='2' align='center'>";
		html += "<a href='javascript:submitform();'><input type='button' value='Login'/></a>";
		html += "<a href='javascript:window.history.back();'><input type='button' value='Cancle'/></a>";
		html += "</td></tr></table></form>";

		return html;
	}

	private String getJsInputCheckSubmit(String formName) {
		String html = "<script type='text/javascript'>function submitform() {";
		html += "if(document." + formName + ".onsubmit && !document." + formName + ".onsubmit()) {";
		html += "return; }";
		html += "if(document.getElementById('id').value == '') { alert('Please input id.'); return; }";
		html += "if(document.getElementById('passwd').value == '') { alert('Please input password.'); return; }";
		html += "document." + formName + ".submit(); } </script>";
		return html;
	}
}
