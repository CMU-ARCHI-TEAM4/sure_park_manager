package com.lge.sureparkmanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.lge.sureparkmanager.utils.Log;

public final class DataBaseConnection {
    private static final String TAG = DataBaseConnection.class.getSimpleName();

    //private String mUrl = "jdbc:mysql://192.168.1.41:3306/sure_park_system?autoReconnect=true&useSSL=true";
    private String mUrl = "jdbc:mysql://128.237.216.217:3306/sure_park_system?autoReconnect=true&useSSL=true";
    
    private String mId = "users";
    private String mPassword = "swarchi1234";

    private Connection mConnection = null;
    private Statement mStatement = null;

    public DataBaseConnection() {
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
