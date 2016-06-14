package com.lge.sureparkmanager.manager;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lge.sureparkmanager.db.DataBaseConnection;
import com.lge.sureparkmanager.utils.Log;

public final class DataBaseManager extends SystemManagerBase {
    private static final String TAG = DataBaseManager.class.getSimpleName();

    private DataBaseConnection mDataBaseConnectionManager;
    private QueryWrapper mQueryWrapper;
    private ResultSet mResultSet;

    protected DataBaseManager() {

    }

    @Override
    protected void init() {
        super.init();

        Log.d(TAG, "init");

        mDataBaseConnectionManager = new DataBaseConnection();
        mQueryWrapper = new QueryWrapper();
    }

    @Override
    protected void clear() {
        super.clear();

        if (mDataBaseConnectionManager != null) {
            mDataBaseConnectionManager.close();
        }

        if (mResultSet != null) {
            try {
                mResultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void reportDeath() {

    }

    public QueryWrapper getQueryWrapper() {
        return mQueryWrapper;
    }

    public class QueryWrapper {
        private static final int CONFIG_NUM = 3;
        private String[] mConfigurationValues = new String[CONFIG_NUM];

        private QueryWrapper() {
            // getting server configuration values.
            getConfiguration();
        }

        private void getConfiguration() {
            final String sql = "SELECT value FROM tb_configuration";
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);

                int idx = 0;
                while (mResultSet.next()) {
                    mConfigurationValues[idx++] = mResultSet.getString("value");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (mResultSet != null) {
                    try {
                        mResultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean isLoginOk(String id, String pw) {
            final String sql = "SELECT id FROM tb_user WHERE id='" + id + "' AND pw='" + pw + "'";
            boolean ret = false;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                mResultSet.last();
                if (mResultSet.getRow() == 1) {
                    ret = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (mResultSet != null) {
                    try {
                        mResultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            return ret;
        }

        public boolean existConfirmationId(String id) {
            boolean ret = true;

            return ret;
        }

        public int getSocketPortNum() {
            return Integer.parseInt(mConfigurationValues[0]);
        }

        public int getGracePeriodTime() {
            return Integer.parseInt(mConfigurationValues[1]);
        }

        public int getHourlyRate() {
            return Integer.parseInt(mConfigurationValues[2]);
        }

        public void setMacAddress(String mac) {
            final String sql = "";
        }
    }
}
