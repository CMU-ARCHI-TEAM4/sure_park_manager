package com.lge.sureparkmanager.manager;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lge.sureparkmanager.db.DataBaseConnection;
import com.lge.sureparkmanager.db.UserInformation;
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
                    final String inSql = "INSERT INTO tb_controller(mac_addr, name) " + "VALUES('" + mac + "','"
                            + nextControllerName + "')";
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
                        final String inSql = "INSERT INTO tb_controller(mac_addr, name) " + "VALUES('" + mac + "','"
                                + nextControllerName + "')";
                        mDataBaseConnectionManager.getStatement().executeUpdate(inSql);
                    } else {
                        Log.d(TAG, "already existing controller");
                    }
                }

                if (newController && nextControllerName != null) {
                    ArrayList<String> plnList = Utils.generateParkingLotNum(parkingLotNum);
                    for (String pln : plnList) {
                        final String parkingLotName = nextControllerName + pln;
                        final String inSql = "INSERT INTO tb_parkinglot(name) " + "VALUES('" + parkingLotName + "')";
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

        public UserInformation getUserInfomation(String id) {
            final String sql = "SELECT * FROM tb_user WHERE id='" + id + "'";
            UserInformation ret = null;

            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                mResultSet.next();

                String _firstName = mResultSet.getString("first_name");
                String _lastName = mResultSet.getString("last_name");
                String _email = mResultSet.getString("email");
                String _phoneNumber = mResultSet.getString("phone_number");
                String _credit_card_num = mResultSet.getString("credit_card_num");
                String _credit_card_date = mResultSet.getString("credit_card_val_date");

                ret = new UserInformation(id, _firstName, _lastName, _email, _phoneNumber, _credit_card_num,
                        _credit_card_date);

                Log.d(TAG, ret.getFristName() + " " + ret.getLastName());

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

        public String addReservation(String id, String parkingFacility, String startTime, String endTime) {

            final String sqlUser = "SELECT idx FROM tb_user WHERE id='" + id + "'";
            try {

                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlUser);
                mResultSet.next();
                String _idx = mResultSet.getString("idx");
                
                Log.d(TAG, "add reservation info with " + id);
                
                String _confirm_id = TokenGenerator.generateToken();
                Log.d(TAG, "add reservation info confirm id  " + _confirm_id);
                
                //find empty parking lot at that time
                final String sqlTime = "SELECT tb_parkinglot_idx FROM tb_reservation WHERE (start_time <= startTime AND end_time > startTime) OR (start_time < endTime AND end_time >= endTime)";
                
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlTime);
                
                Log.d(TAG, "out of lots" + mResultSet.getFetchSize());
                
                // check the number of lots
                if (mResultSet.getFetchSize() > 4) {
                	Log.d(TAG, "out of lots");
                }
                
                mResultSet.next();
                String parkinglot = mResultSet.getString("tb_parkinglot_idx");
                
                
                
                //TODO; get the number of lot in parkingFacility
                String _empty_lot = "1";
                
                final String sql = "INSERT into tb_reservation (tb_user_idx, start_time, end_time, tb_parkinglot_idx, confirm_id) VALUES('"
                        + _idx + "', '" + startTime + "', '" + endTime + "', '" + _empty_lot + "', '" + _confirm_id + "')";

                mDataBaseConnectionManager.getStatement().executeUpdate(sql);

                return _confirm_id;

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

            return null;
        }
        
        public List<String> getListOfFacility() {
        	
        	//TODO; fix table name and column name
            final String sql = "SELECT * FROM tb_user";
            ArrayList<String> ret = new ArrayList<String>();
            
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);

                while (mResultSet.next()) {
                	ret.add(mResultSet.getString("facility"));
                }
                
                Log.d(TAG, "Get list of facility" + ret.size());
                
                return ret;

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
