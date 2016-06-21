package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lge.sureparkmanager.manager.CommandManager;
import com.lge.sureparkmanager.manager.DataBaseManager;
import com.lge.sureparkmanager.manager.NetworkManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.WebSession;

public class CheckConfirmation extends HttpServlet {
    private static final String TAG = CheckConfirmation.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    private DataBaseManager mDataBaseManager;
    private String mParkingFacilityName;
    private int mParkingLotNum;
    private String mMacAddr;
    private String[] mConfirmationInfo;
    private String mConfirmationId;

    private boolean mConfirmedOk = false;

    public CheckConfirmation() {
        super();
        Log.d(TAG, "init");
        mDataBaseManager = SystemManager.getInstance().getDataBaseManager();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String sessionUserId = (String) request.getSession()
                .getAttribute(WebSession.SESSION_USERID);
        final String hiddenParam = request.getParameter("pf_h");
        final String openGate = request.getParameter("open_gate");
        mConfirmationId = request.getParameter("confirm_id");

        if (mConfirmationId != null && mConfirmationId.length() > 0) {
            mConfirmationInfo = mDataBaseManager.getQueryWrapper()
                    .isValidConfirmationId(mConfirmationId);
            if (mConfirmationInfo != null && mConfirmationInfo.length == 4) {
                mConfirmedOk = true;
            }
        }

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
            printWriter.write(getBodyHtml());
            printWriter.write(Html.getHtmlFooter());

            if ("1".equals(openGate)) {
                printWriter.write(openGateAndExitPage());
            }
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

    private String getBodyHtml() {
        String html = "<h1 class=\"center\">Parking facility - " + mParkingFacilityName + "</h1>";
        html += "<form action='ccf' name='ccf' method='post'>";
        html += "<input type='hidden' name='pf_h' value='" + getHiddenParamPFH() + "'/>";
        html += "<table class='centerTable' width='500'>";
        html += "<tr><td align='right'>Please input confirmation information.&nbsp;&nbsp;</td></tr>";
        html += "<tr><td align='right'>";
        html += "<input type='text' name='confirm_id' id='confirm_id' size='30' ";
        html += "value='" + (mConfirmationId == null ? "" : mConfirmationId) + "' />";
        html += "</td></tr><tr><td align='right'>";
        if (mConfirmedOk) {
            html += "<input type='hidden' name='open_gate' value='1'/>";
            html += "<font color='yellow'>OK</font><input type='submit' value='Open'/>";
            mConfirmedOk = false;
        } else {
            html += "<input type='submit' value='Check'/>";
        }
        html += "<a href=\"javascript:document.pfd.submit();\">";
        html += "<input type='button' value='Cancel'/></a></td>";
        html += "</tr></table></form>";
        html += "<form action='pfd' name='pfd' method='post'>";
        html += "<input type='hidden' name='pf_h' value='" + getHiddenParamPFH() + "'/>";
        html += "</form>";

        return html;
    }

    private String getHiddenParamPFH() {
        return mParkingFacilityName + "^" + mParkingLotNum + "^" + mMacAddr;
    }

    private String openGateAndExitPage() {
        CommandManager cm = (CommandManager) SystemManager.getInstance()
                .getManager(SystemManager.COMMAND_MANAGER);
        NetworkManager nm = (NetworkManager) SystemManager.getInstance()
                .getManager(SystemManager.NETWORK_MANAGER);
        nm.sendMessageToTarget(mMacAddr, cm.generateOpenEntryGateCommand(mMacAddr,
                mConfirmationInfo[0], mConfirmationInfo[3]));

        String html = "<script type='text/javascript'>";
        html += "document.pfd.submit();";
        html += "</script>";

        return html;
    }
}
