package com.lge.sureparkmanager.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.lge.sureparkmanager.db.DataBaseConnection;
import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.Utils;

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

    private int getResultSetSize(ResultSet rs) {
        int size = 0;
        if (rs != null) {
            try {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return size;
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

        public void setParkInfo(String mac, int parkingLotNum) {
            final String sql = "SELECT * FROM tb_controller ORDER BY idx ASC";
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                int rowCount = getResultSetSize(mResultSet);

                boolean newController = false;
                String nextControllerName = null;

                // first new device connected.
                if (rowCount == 0) {
                    newController = true;
                    nextControllerName = "A";
                    final String inSql = "INSERT INTO tb_controller(mac_addr, name) " +
                            "VALUES('" + mac + "','" + nextControllerName + "')";
                    mDataBaseConnectionManager.getStatement().executeUpdate(inSql);
                } else {
                    boolean exist = false;
                    String macAddr = null;
                    String name = null;

                    while (mResultSet.next()) {
                        macAddr = mResultSet.getString("mac_addr");
                        name = mResultSet.getString("name");
                        if (macAddr.equals(mac)) { // exist
                            exist = true;
                        }
                    }

                    if (!exist && macAddr != null && name != null) {
                        newController = true;
                        nextControllerName = Utils.getNextControllerName(name);
                        final String inSql = "INSERT INTO tb_controller(mac_addr, name) " +
                                "VALUES('" + mac + "','" + nextControllerName + "')";
                        mDataBaseConnectionManager.getStatement().executeUpdate(inSql);
                    } else {
                        Log.d(TAG, "already existing controller");
                    }
                }

                if (newController && nextControllerName != null) {
                    ArrayList<String> plnList = Utils.generateParkingLotNum(parkingLotNum);
                    for (String pln : plnList) {
                        final String parkingLotName = nextControllerName + pln;
                        final String inSql = "INSERT INTO tb_parkinglot(name) " +
                                "VALUES('" + parkingLotName + "')";
                        mDataBaseConnectionManager.getStatement().executeUpdate(inSql);
                    }
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
    }
}
