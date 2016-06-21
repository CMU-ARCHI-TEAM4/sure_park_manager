package com.lge.sureparkmanager.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lge.sureparkmanager.manager.AliveCheckerManager;
import com.lge.sureparkmanager.manager.CommandManager;
import com.lge.sureparkmanager.manager.Commands;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;

public class InfoProvider extends HttpServlet implements CommandManager.CommandListener {
    private static final String TAG = InfoProvider.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    private static final long ENTRY_GATE_TIME_OUT = 5000;

    private CommandManager mCommandManager;
    private boolean mIsArrivedEntryGate = false;

    public InfoProvider() {
        super();
        Log.d(TAG, "init");
        mCommandManager = (CommandManager) SystemManager.getInstance()
                .getManager(SystemManager.COMMAND_MANAGER);
        mCommandManager.registerListener(this, Commands.CMD_ENTRY_SENSOR);
    }

    @Override
    public void onCommand(String macAddr, int mainCmd, int subCmd) {
        Log.d(TAG, "onCommand: " + macAddr + " " + mainCmd + " " + subCmd);
        if (mainCmd == Commands.CMD_ENTRY_SENSOR) {
            if (subCmd == Commands.CMD_ENTRY_SENSOR_DET) {
                mIsArrivedEntryGate = true;
                (new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(ENTRY_GATE_TIME_OUT);
                        } catch (InterruptedException e) {
                        }
                        mIsArrivedEntryGate = false;
                    }
                }).start();
            } else {
                mIsArrivedEntryGate = false;
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String pfName = request.getParameter("pfn");
        final String mac = request.getParameter("mac");
        AliveCheckerManager acm = (AliveCheckerManager) SystemManager.getInstance()
                .getManager(SystemManager.ALIVE_CHECKER_MANAGER);

        String rspStr = acm.isAlive(mac) ? "1" : "0";

        //Log.d(TAG, "doGet: " + pfName);

        if (pfName == null || pfName.length() == 0) {
            Html.executeJsGoBack(response);
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = null;
        ResultSet rs = null;
        try {
            printWriter = response.getWriter();

            final String pfs = SystemManager.getInstance().getDataBaseManager().getQueryWrapper()
                    .getParkStatusInfo(pfName);
            final String sql = "SELECT entry_gate, exit_gate, parkinglot_num"
                    + " FROM tb_controller WHERE name='" + pfName + "'";
            rs = SystemManager.getInstance().getDataBaseManager().getStatement().executeQuery(sql);
            while (rs.next()) {
                rspStr += " " + rs.getString("entry_gate");
                rspStr += " " + rs.getString("exit_gate");
                rspStr += " " + rs.getString("parkinglot_num");
            }
            rspStr += " " + (mIsArrivedEntryGate ? 1 : 0);
            rspStr += " " + pfs;
            Log.d(TAG, rspStr);
            printWriter.write(rspStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // Ignored.
                }
            }
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
