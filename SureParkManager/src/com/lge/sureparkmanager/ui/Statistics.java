package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.WebSession;

public class Statistics extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Statistics() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String sessionUserId = (String) request.getSession()
                .getAttribute(WebSession.SESSION_USERID);
        if (!WebSession.SESSION_USERID_ADMIN.equals(sessionUserId)) {
            Html.executeJsGoBack(response);
        }

        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(Html.getHtmlHeader());
            pw.write(request.getSession().getAttribute("id") + "");
            pw.write(request.getSession().getAttribute("test") + "");
            pw.write(Html.getHtmlFooter());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
