package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;

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

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final String TAG = Login.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
        Log.d(TAG, "init");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final StringBuffer requestUrl = request.getRequestURL();

        request.setCharacterEncoding("euc-kr");
        DataBaseManager dbm = (DataBaseManager) SystemManager.getInstance().getManager(
                SystemManager.DATABASE_MANAGER);

        String id = request.getParameter("id");
        String pw = request.getParameter("passwd");

        if (dbm.getQueryWrapper().isLoginOk(id, pw)) {
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
                printWriter.write(getJsInputCheckSubmit("login"));

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

    private String getLoginHtml(String title) {
        String html = "<form action='login' name='login' method='post'>";
        html += "<table class='centerTable'>";
        html += "<tr ><td colspan='2' align='center'><h1>" + title + "</h1></td></tr>";
        html += "<tr><td align='center'>ID</td>";
        html += "<td><input type='text' name='id' id='id' /></td>";
        html += "</tr><tr><td align='center'>PW</td>";
        html += "<td><input type='password' name='passwd' id='passwd' /></td></tr>";
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
