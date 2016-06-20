package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lge.sureparkmanager.manager.DataBaseManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.Utils;
import com.lge.sureparkmanager.utils.WebSession;

public class Statistics extends HttpServlet {
    private static final String TAG = Statistics.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    private DataBaseManager mDataBaseManager;
    private Map<String, String> mMonthlySales = new HashMap<String, String>();
    private String mSelectedYear;
    private String mSelectedMonth;

    public Statistics() {
        super();
        Log.d(TAG, "init");

        mDataBaseManager = SystemManager.getInstance().getDataBaseManager();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String sessionUserId = (String) request.getSession()
                .getAttribute(WebSession.SESSION_USERID);
        if (!WebSession.SESSION_USERID_ADMIN.equals(sessionUserId)) {
            Html.executeJsGoBack(response);
        }

        mSelectedYear = request.getParameter("year");
        mSelectedMonth= request.getParameter("month");

        if (mSelectedYear != null && mSelectedYear.length() > 0
                && mSelectedMonth != null & mSelectedMonth.length() > 0) {
            final String firstTime = Utils.getFirstTime(mSelectedYear, mSelectedMonth);
            final String lastTime = Utils.getLastTime(mSelectedYear, mSelectedMonth);
            final String sql = "SELECT name, SUM(fee) AS fee FROM tb_history INNER JOIN "
                    + "tb_parkinglot ON tb_parkinglot_idx=tb_parkinglot.idx "
                    + "WHERE start_time >= '" + firstTime + "' AND "
                    + "end_time <= '" + lastTime + "' GROUP BY name ORDER BY name ASC";

            ResultSet rs = null;
            Map<String, String> map = new HashMap<String, String>();
            try {
                rs = mDataBaseManager.getStatement().executeQuery(sql);
                while (rs.next()) {
                    map.put(rs.getString("name"), rs.getString("fee"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }
            }

            mMonthlySales = new TreeMap<String, String>(map);
        }

        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(Html.getHtmlHeader());
            pw.write(getStatisticsHtml());
            pw.write(Html.getHtmlFooter());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        mMonthlySales.clear();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String getStatisticsHtml() {
        String html = "<h1 class=\"center\">Parking facility statistics</h1>";
        html += "<link rel='stylesheet' href='fcf/Contents/Style.css' type='text/css' />";
        html += "<script src='fcf/JSClass/FusionCharts.js'></script>";
        html += "<form action='statistics' method='post'>";
        html += "<table class='centerTable' width='800'><tr><td align='right'>";
        html += "Year: " + getYearHtml() + "&nbsp;&nbsp;&nbsp; Month: " + getMonthHtml();
        html += "<input type='submit' value='Search'/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        html += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>";
        html += getGraphDiv();
        html += "</td></tr></table></form>";

        return html;
    }

    private String getGraphDiv() {
        String html = "<div id='chartdiv' align='center'>No data.</div>";
        if (mMonthlySales.size() > 0) {
            html += "<script type='text/javascript'>";
            html += "var chart = new FusionCharts('fcf/Charts/FCF_Column2D.swf','ChartId', '600', '350');";
            html += "chart.setDataXML(\"" + getDataXml() + "\");";
            html += "chart.render('chartdiv');</script>";
        }

        return html;
    }

    private String getYearHtml() {
        final int curYear = Calendar.getInstance().get(Calendar.YEAR);
        String html = "<select name='year' style=\"width: 150px\">";

        for (int i = 2000; i < 3000; i++) {
            html += "<option value='" + i + "' ";
            if (mSelectedYear != null && mSelectedYear.length() > 0) {
                if (Integer.parseInt(mSelectedYear) == i) {
                    html += "selected";
                }
            } else if (curYear == i) {
                html += "selected";
            }
            html += ">" + i + "</option>";
        }
        html += "</select>";

        return html;
    }

    private String getMonthHtml() {
        final int curMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String html = "<select name='month' style=\"width: 150px\">";

        for (int i = 1; i < 13; i++) {
            html += "<option value='" + i + "' ";
            if (mSelectedMonth != null && mSelectedMonth.length() > 0) {
                if (Integer.parseInt(mSelectedMonth) == i) {
                    html += "selected";
                }
            } else if (curMonth == i) {
                html += "selected";
            }
            html += ">" + i + "</option>";
        }
        html += "</select>";

        return html;
    }

    private String getDataXml() {
        if (mMonthlySales == null || mMonthlySales.size() == 0) {
            return "";
        }

        String dataXml = "";
        int totalSales = 0;
        for (Map.Entry<String, String> e : mMonthlySales.entrySet()) {
            final String v = e.getValue();
            totalSales += Integer.parseInt(v);
            dataXml += "<set name='" + e.getKey() + "' value='" + e.getValue() + "' color='"
                    + Utils.generateColor(new Random()) + "' />";
        }

        String xml = "<graph caption='Monthly sales $" + totalSales
                + "' xAxisName='Month' yAxisName='Sales' ";
        xml += "decimalPrecision='0' formatNumberScale='0'>";
        xml += dataXml;
        xml += "</graph>";

        return xml;
    }
}
