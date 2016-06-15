package com.lge.sureparkmanager.manager;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lge.sureparkmanager.db.DataBaseConnection;
import com.lge.sureparkmanager.db.UserInformation;
import com.lge.sureparkmanager.utils.Log;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

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
                
                ret = new UserInformation(id, _firstName, _lastName, _email, _phoneNumber, _credit_card_num, _credit_card_date);
                
                System.out.println(ret.getFristName() + " " + ret.getLastName());
                
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
        
        public String addReservation(String id, String startTime, String endTime) {
        	
        	 
        	final String sqlUser = "SELECT idx FROM tb_user WHERE id='" + id + "'";
        	 try {
        		 
				mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlUser);
				mResultSet.next();
				String _idx = mResultSet.getString("idx");
				System.out.println("idx !!!!!!!" + _idx);
				
				//TODO: choose parking lot, get confirmation id
				String _confirm_id = "A0001";
				final String sql = "INSERT into tb_reservation (tb_user_idx, start_time, end_time, tb_parkinglot_idx, confirm_id) VALUES('" + _idx + "', '" + startTime + "', '" + endTime + "', '1', '" + _confirm_id + "')";
		           
        	  
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
