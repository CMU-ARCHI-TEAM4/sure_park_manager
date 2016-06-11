package com.lge.sureparkmanager.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.lge.sureparkmanager.utils.Log;

public final class DataBaseConnectionManager {
    private static final String TAG = DataBaseConnectionManager.class.getSimpleName();

    private String mUrl = "jdbc:mysql://localhost/sure_park_system?autoReconnect=true&useSSL=true";
    private String mId = "root";
    private String mPassword = "swarchi1234";

    private Connection mConnection = null;
    private Statement mStatement = null;

    public DataBaseConnectionManager() {
        init();
    }

    public Statement getStatement() {
        return mStatement;
    }

    private void init() {
        Log.d(TAG, "init");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            mConnection = DriverManager.getConnection(mUrl, mId, mPassword);
            mStatement = mConnection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (mStatement != null) {
                mStatement.close();
            }
            if (mConnection != null) {
                mConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
