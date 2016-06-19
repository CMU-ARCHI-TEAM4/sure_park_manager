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

public class ParkingFacilityDetail extends HttpServlet {
    private static final String TAG = ParkingFacilityDetail.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    private DataBaseManager mDataBaseManager;
    private String mParkingFacilityName;
    private int mParkingLotNum;
    private String mMacAddr;

    public ParkingFacilityDetail() {
        super();
        Log.d(TAG, "init");

        mDataBaseManager = (DataBaseManager) SystemManager.getInstance()
                .getManager(SystemManager.DATABASE_MANAGER);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String sessionUserId = (String) request.getSession()
                .getAttribute(WebSession.SESSION_USERID);
        final String hiddenParam = request.getParameter("pf_h");

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();

            if (!WebSession.SESSION_USERID_ATTENDANT.equals(sessionUserId) || hiddenParam == null
                    || hiddenParam.length() == 0) {
                Html.executeJsGoBack(response);
            }

            final String[] split = hiddenParam.split("\\^");
            mParkingFacilityName = split[0];
            mParkingLotNum = Integer.parseInt(split[1]);
            mMacAddr = split[2];

            printWriter.write(Html.getHtmlHeader());
            printWriter.write(getJsInfoProviderRequest());
            printWriter.write(getParkingFacilityDetailInfoHtml());
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

    private String getParkingFacilityDetailInfoHtml() {
        final ArrayList<String> parkingLotInfos = mDataBaseManager.getQueryWrapper()
                .getParkingLotInfo(mParkingFacilityName);
        String html = "<h1 class=\"center\">Parking facility - " + mParkingFacilityName + "</h1>";

        html += "<table class='centerTable' width='500'><tr>";
        html += "<td rowspan='3' align='center'>";
        html += "<img src='images/pf_dave.png' width='30%' height='30%' />";
        html += "</td><td width='200' id='alive'>Device:";
        html += "</td></tr><tr><td width='200' id='entry_gate'>Entry Gate:";
        html += "</td></tr><tr><td width='200' id='exit_gate'>Exit Gate:";
        html += "</td></tr></table>";

        html += "<table class=\"parking_facility_detail_table\" border=\"1\">";
        if (mParkingLotNum == parkingLotInfos.size()) {
            for (int i = 0; i < parkingLotInfos.size(); i++) {
                final String[] split = parkingLotInfos.get(i).split("\\^");
                final String lotName = split[1];
                html += (i % 5 == 0) ? "<tr>" : "";
                html += "<td id='" + lotName + "'>" + lotName + "</td>";
                html += (i > 0 && i % 5 == 4) ? "</tr>" : "";
            }
        }
        html += "</table>";

        return html;
    }

    private String getJsInfoProviderRequest() {
        String html = "";

        html += "<script type=\"text/javascript\">";
        html += "var myVar = setInterval(getInfo, 1000);";
        html += "function getInfo() {";
        html += "var xhr = new XMLHttpRequest();";
        html += "xhr.open('GET', \"/s/infop?pfn=" + mParkingFacilityName + "&mac=" + mMacAddr
                + "\", true);";
        html += "xhr.send();";
        html += "xhr.onreadystatechange = function() {";
        html += "if (xhr.readyState == 4 && xhr.status == 200) {";
        html += "var data = xhr.responseText.split(\" \");";
        html += "alive = data[0];entry_gate = data[1];exit_gate = data[2];parkinglot_num = data[3];";
        html += "var parkinglot_status = data[4].split(\"^\");";
        html += "var h;";
        html += "if (alive == 1) {";
        html += "h = \"Device: <font color='yellow'>OK</font>\"";
        html += "} else {h = \"Device: <font color='red'>DEAD</font>\";}";
        html += "document.getElementById(\"alive\").innerHTML = h;";
        html += "if (entry_gate == 1) {";
        html += "h = \"Entry Gate:<font color='red'>close</font>\";";
        html += "} else {h = \"Entry Gate:<font color='yellow'>open</font>\";}";
        html += "document.getElementById(\"entry_gate\").innerHTML = h;";
        html += "if (exit_gate == 1) {";
        html += "h = \"Exit Gate:<font color='red'>close</font>\";";
        html += "} else {h = \"Exit Gate:<font color='yellow'>open</font>\";}";
        html += "document.getElementById(\"exit_gate\").innerHTML = h;";
        html += "for (i = 0; i < parkinglot_num; i++) {";
        html += "var e = parkinglot_status[i].split(\":\");";
        html += "if (e[1] == 1) {document.getElementById(e[0]).innerHTML = e[0] + \"</br><font color='red'>full</font>\";";
        html += "} else {document.getElementById(e[0]).innerHTML = e[0] + \"</br><font color='yellow'>empty</font>\";";
        html += "}}}}}</script>";

        return html;
    }
}