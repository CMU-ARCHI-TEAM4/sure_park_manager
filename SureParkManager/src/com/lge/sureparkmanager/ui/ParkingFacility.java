package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lge.sureparkmanager.manager.DataBaseManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.WebSession;

public class ParkingFacility extends HttpServlet {
    private static final String TAG = ParkingFacility.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    private DataBaseManager mDataBaseManager;

    public ParkingFacility() {
        super();
        Log.d(TAG, "init");

        mDataBaseManager = (DataBaseManager) SystemManager.getInstance()
                .getManager(SystemManager.DATABASE_MANAGER);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String sessionUserId = (String) request.getSession()
                .getAttribute(WebSession.SESSION_USERID);

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();

            if (!WebSession.SESSION_USERID_ATTENDANT.equals(sessionUserId)) {
                Html.executeJsGoBack(response);
            }

            printWriter.write(Html.getHtmlHeader());
            printWriter.write(Html.getJsSubmitForm("pf"));
            printWriter.write(getParkingFacilityInfoHtml());

            printWriter.write(Html.getHtmlFooter());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String getParkingFacilityInfoHtml() {
        String html = "<h1 class=\"center\">Parking facility status</h1>";
        html += "<form name=\"pf\" action=\"pfd\" method=\"post\">";
        html += "<input type=\"hidden\" id=\"pf_h\" name=\"pf_h\" value=\"\">";
        html += "<table class=\"centerTable\">";
        html += "<tr><td align=\"center\">";

        final ArrayList<String> pfi = mDataBaseManager.getQueryWrapper().getParkingFacilityInfo();

        for (int i = 0; i < pfi.size(); i++) {
            final String[] split = pfi.get(i).split("\\^");
            final String macAddr = split[0];
            final String name = split[1];
            final String parkingLotNum = split[2];
            final String arg = name + "^" + parkingLotNum + "^" + macAddr;

            html += (i % 4 == 0) ? "<tr>" : "";
            html += "<td align=\"center\"><a href=\"javascript:submitform('" + arg + "')\">";
            html += "<img src=\"images/parking_icon.png\" width=\"40%\" height=\"50%\" /></a>";
            html += "</br>" + macAddr + "</br>" + name + "</td>";
            html += (i > 0 && i % 4 == 3) ? "</tr>" : "";
        }
        html += "</table></form>";

        return html;
    }
}
