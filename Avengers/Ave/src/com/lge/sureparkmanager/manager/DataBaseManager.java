package com.lge.sureparkmanager.manager;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lge.sureparkmanager.utils.Log;

public final class DataBaseManager extends SystemManagerBase {
    private static final String TAG = DataBaseManager.class.getSimpleName();

    private DataBaseConnectionManager mDataBaseConnectionManager;
    private QueryWrapper mQueryWrapper;
    private ResultSet mResultSet;

    protected DataBaseManager() {

    }

    @Override
    protected void init() {
        super.init();

        Log.d(TAG, "init");

        mDataBaseConnectionManager = new DataBaseConnectionManager();
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
    }
}
