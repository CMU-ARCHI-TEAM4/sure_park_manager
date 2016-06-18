package com.lge.sureparkmanager.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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

        /**
         * get user information
         * @param id
         * @return
         */
        public UserInformation getUserInfomation(String id) {
            final String sql = "SELECT * FROM tb_user WHERE id='" + id + "'";
            UserInformation ret = null;

            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                mResultSet.next();

                final String _firstName = mResultSet.getString("first_name");
                final String _lastName = mResultSet.getString("last_name");
                final String _email = mResultSet.getString("email");
                final String _phoneNumber = mResultSet.getString("phone_number");
                final String _credit_card_num = mResultSet.getString("credit_card_num");
                final String _credit_card_date = mResultSet.getString("credit_card_val_date");

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
        
        /**
         * get the total number of lots in specific facility.
         * @param facility
         * @return
         */
        public int getTotalNumberOfLots(String facility) {
        	
        	// find out total number of lots
            final String sql = "SELECT name, parkinglot_num FROM tb_controller";
            try {

                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                String name = null;
                while (mResultSet.next()) {
                    name = mResultSet.getString("name");
                    Log.d(TAG, "facility " + name);
                    if (facility.equalsIgnoreCase(name)) {
                    	final int ret = mResultSet.getInt("parkinglot_num");
                    	Log.d(TAG, "the number of lots : " + ret);
                    	return ret;
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

            Log.e(TAG, "There is no matched facility in DB");
            return 0;
        }

        /**
         * add reservation information on DB
         * @param id
         * @param parkingFacility
         * @param startTime
         * @param endTime
         * @return
         */
        public String addReservation(String id, String parkingFacility, String startTime, String endTime) {

            final String sqlUser = "SELECT idx FROM tb_user WHERE id='" + id + "'";
            ResultSet resevedLotResult = null;
            ResultSet mResultSet3 = null;
            ResultSet mResultSet2 = null;
            try {

                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlUser);
                mResultSet.next();
                String _idx = mResultSet.getString("idx");
                
                Log.d(TAG, "add reservation info with " + id);
                
                String _confirm_id = TokenGenerator.generateToken();
                Log.d(TAG, "add reservation info confirm id  " + _confirm_id);
                
				//mResultSet.close();
				
				final int totalLot = getTotalNumberOfLots(parkingFacility);
				
				//find empty parking lot at that time
				final String sqlTime = "SELECT tb_parkinglot_idx FROM tb_reservation WHERE (start_time <= '" + startTime
						+ "' AND end_time > '" + startTime + "' ) OR (start_time < '" + endTime + "'  AND end_time >= '"
						+ endTime + "')";
				
				Log.d(TAG, "sql for reserved lots : " + sqlTime);
                
				mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlTime);
                final int reservedTotalLot = mResultSet.getFetchSize();
                Log.d(TAG, "reserved lots number : " + reservedTotalLot);
                
                
                
                // check the number of lots
                if (reservedTotalLot > totalLot) {
                	Log.d(TAG, "out of lots");
                	return null;
                }
                
                int[] lots = new int[totalLot+1]; // from 1 to totalLot
                ArrayList<String> parkinglotIdxList= new ArrayList<String>();
                while (mResultSet.next()) {
                	parkinglotIdxList.add(mResultSet.getString("tb_parkinglot_idx"));
                }
                
                Iterator<String> it = parkinglotIdxList.iterator();
                while (it.hasNext()) {
                    String parkinglot_idx = (String) it.next();
                    Log.d(TAG, "parkinglot_idx " + parkinglot_idx);
                    String sqlParkingLot =  "SELECT name FROM tb_parkinglot WHERE idx='" + parkinglot_idx + "'";

                    mResultSet2 = mDataBaseConnectionManager.getStatement().executeQuery(sqlParkingLot);
                    mResultSet2.next();
                    String name = mResultSet2.getString("name");
                    //mResultSet2.close();
                    
                    String tt = name.substring(name.length()-5);
                    
                    Log.d(TAG, "lots name : " + name + " length : " + name.length());
                    int reservedLot = Integer.parseInt(tt);
                    Log.d(TAG, "the number of lots "  + reservedLot);
                    //int reservedLot = Integer.getInteger(name.substring(name.length()-5));
                    lots[reservedLot] = Integer.parseInt(parkinglot_idx); //occupied lots
                    Log.d(TAG, "lots[reservedLot] "  + lots[reservedLot]);
                    
                }
                
                // get the number of lot in parkingFacility
                int _empty_lot = -1;
                
                for (int j=1; j<=totalLot; j++) {
                	if (lots[j] == 0) {
                		Log.d(TAG, "find out empty lot " + lots[j]);
                		_empty_lot = j;
                		break;
                	}
                }
                
                if (_empty_lot == -1) {
                	// no empty space
                	Log.d(TAG, "can't reserve the space");
                	Log.e(TAG, "can't reserve the space");
                	return null;
                }
                
                // find out tb_parkinglot_idx
                String idx = String.format("%S%05d", parkingFacility, _empty_lot);
                Log.d(TAG, "find out idx from tb_parkinglot "  + idx);
                String sqlParkingidx =  "SELECT idx FROM tb_parkinglot WHERE name='" + idx + "'";
                mResultSet3 = mDataBaseConnectionManager.getStatement().executeQuery(sqlParkingidx);
                mResultSet3.next();
                
                final String sql = "INSERT into tb_reservation (tb_user_idx, start_time, end_time, tb_parkinglot_idx, confirm_id) VALUES('"
                        + _idx + "', '" + startTime + "', '" + endTime + "', '" + mResultSet3.getString("idx") + "', '" + _confirm_id + "')";

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
                
                if (resevedLotResult != null) {
                    try {
                    	resevedLotResult.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                if (mResultSet3 != null) {
                    try {
                    	mResultSet3.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
        
        public List<String> getListOfFacility() {
        	
        	//TODO; fix table name and column name
            final String sql = "SELECT name FROM tb_controller";
            ArrayList<String> ret = new ArrayList<String>();
            
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);

                while (mResultSet.next()) {
                	ret.add(mResultSet.getString("name"));
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
