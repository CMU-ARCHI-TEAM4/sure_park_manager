package com.lge.sureparkmanager.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

    public Statement getStatement() {
        return mDataBaseConnectionManager.getStatement();
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
        private LogManager lm = (LogManager) SystemManager.getInstance()
                .getManager(SystemManager.LOG_MANAGER);

        final String sqlCommit = "SET autocommit = 1";
        final String sqlUpdate = "SET SQL_SAFE_UPDATES = 0";

        private QueryWrapper() {

        }

        private String getConfiguration(String where) {
            String ret = null;
            final String sql = "SELECT value FROM tb_configuration WHERE name='" + where + "'";
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    ret = mResultSet.getString("value");
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

        public String getParkingFacilityName(String mac) {
            final String sql = "SELECT name FROM tb_controller WHERE mac_addr='" + mac + "'";
            String parkingFacilityName = null;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    parkingFacilityName = mResultSet.getString("name");
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

            return parkingFacilityName;
        }

        public String getReservationParkingLotName(String confirmId) {
            final String sql = "SELECT name FROM sure_park_system.tb_reservation INNER JOIN "
                    + "tb_parkinglot ON tb_parkinglot_idx=tb_parkinglot.idx WHERE " + "confirm_id='"
                    + confirmId + "'";
            String parkingLotName = null;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    parkingLotName = mResultSet.getString("name");
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

            return parkingLotName;
        }

        public void reallocationParkingLot(String confirmId, String oriParkingLotName,
                String newParkingLotName) {
            String sql = null;
            final int oriParkingLotTbIdx = getParkingLotIdx(oriParkingLotName);
            final int newParkingLotTbIdx = getParkingLotIdx(newParkingLotName);

            try {
                ArrayList<String> idxs = new ArrayList<String>();
                sql = "SELECT idx FROM tb_reservation WHERE tb_parkinglot_idx='"
                        + oriParkingLotTbIdx + "'";
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    idxs.add(mResultSet.getString("idx"));
                }
                sql = "UPDATE tb_reservation SET tb_parkinglot_idx='" + oriParkingLotTbIdx
                        + "' WHERE tb_parkinglot_idx='" + newParkingLotTbIdx + "'";
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);

                for (String idx : idxs) {
                    sql = "UPDATE tb_reservation SET tb_parkinglot_idx='" + newParkingLotTbIdx
                            + "' WHERE idx='" + idx + "'";
                    mDataBaseConnectionManager.getStatement().executeUpdate(sql);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public String getConfirmationId(int parkingLotTbIdx) {
            final String sql = "SELECT confirm_id FROM sure_park_system.tb_reservation "
                    + "WHERE tb_parkinglot_idx='" + parkingLotTbIdx + "' LIMIT 1";
            String confirmationId = null;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    confirmationId = mResultSet.getString("confirm_id");
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

            return confirmationId;
        }

        public String getParkingFacilityMacAddr(String pfn) {
            final String sql = "SELECT mac_addr FROM tb_controller WHERE name='" + pfn + "'";
            String parkingFacilityMacAddr = null;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    parkingFacilityMacAddr = mResultSet.getString("mac_addr");
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

            return parkingFacilityMacAddr;
        }

        public int getParkingLotIdx(String pln) {
            final String sql = "SELECT idx FROM tb_parkinglot WHERE name='" + pln + "'";
            int idx = -1;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    idx = mResultSet.getInt("idx");
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

            return idx;
        }

        public ArrayList<String> getTotalParkingLotName() {
            ArrayList<String> totalParkingLotName = new ArrayList<String>();
            final String sql = "SELECT name FROM tb_parkinglot";
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    totalParkingLotName.add(mResultSet.getString("name"));
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
            return totalParkingLotName;
        }

        public int getSocketPortNum() {
            return Integer.parseInt(getConfiguration("port_num"));
        }

        public int getGracePeriodTime() {
            return Integer.parseInt(getConfiguration("grace_period"));
        }

        public int getHourlyRate() {
            return Integer.parseInt(getConfiguration("hourly_rate"));
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
                    final String inSql = "INSERT INTO tb_controller(mac_addr, name, parkinglot_num) "
                            + "VALUES('" + mac + "','" + nextControllerName + "','" + parkingLotNum
                            + "')";
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
                        final String inSql = "INSERT INTO tb_controller(mac_addr, name, parkinglot_num) "
                                + "VALUES('" + mac + "','" + nextControllerName + "','"
                                + parkingLotNum + "')";
                        mDataBaseConnectionManager.getStatement().executeUpdate(inSql);
                    } else {
                        Log.d(TAG, "already existing controller");
                    }
                }

                if (newController && nextControllerName != null) {
                    ArrayList<String> plnList = Utils.generateParkingLotNum(parkingLotNum);
                    for (String pln : plnList) {
                        final String parkingLotName = nextControllerName + pln;
                        final String inSql = "INSERT INTO tb_parkinglot(name) " + "VALUES('"
                                + parkingLotName + "')";
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

        public void setParkEntryGateInfo(String mac, String status) {
            final String sql = "UPDATE tb_controller SET entry_gate='" + status
                    + "' WHERE mac_addr='" + mac + "'";
            try {
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);
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

        public void setParkExitGateInfo(String mac, String status) {
            final String sql = "UPDATE tb_controller SET exit_gate='" + status
                    + "' WHERE mac_addr='" + mac + "'";
            try {
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);
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

        public String getParkStatusInfo(String pfn) {
            String rsp = "";
            String sql = "SELECT idx, name, status FROM tb_parkinglot WHERE name LIKE '" + pfn
                    + "%'";
            ArrayList<String> names = new ArrayList<String>();
            ArrayList<String> status = new ArrayList<String>();
            ArrayList<String> idxs = new ArrayList<String>();
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    names.add(mResultSet.getString("name"));
                    status.add(mResultSet.getString("status"));
                    idxs.add(mResultSet.getString("idx"));
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

            for (int i = 0; i < idxs.size(); i++) {
                String idx = idxs.get(i);
                String name = names.get(i);
                String stat = status.get(i);
                sql = "SELECT start_time FROM tb_history WHERE tb_parkinglot_idx='" + idx
                        + "' AND start_time IS NOT NULL AND end_time IS NULL "
                        + "ORDER BY idx LIMIT 1";
                String rslt = null;
                try {
                    mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                    while (mResultSet.next()) {
                        rslt = mResultSet.getString("start_time");
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

                int gracePeriod = getGracePeriodTime();
                String curTime = Utils.getCurrentDateTime(Utils.DATE_FORMAT);
                String gracePeriodTime = Utils.getCurrentTimeSubMins(gracePeriod);
                sql = "SELECT idx FROM tb_reservation WHERE tb_parkinglot_idx='" + idx
                        + "' AND start_time <= '" + curTime + "' AND start_time"
                        + " >= '" + gracePeriodTime + "'";
                String rslt2 = null;
                try {
                    mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                    while (mResultSet.next()) {
                        rslt2 = mResultSet.getString("idx");
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

                rsp += name + ":" + stat + ":";
                if (rslt == null || "0".equals(stat)) {
                    rsp += "0";
                } else {
                    long diff = 0;
                    SimpleDateFormat format = new SimpleDateFormat(Utils.DATE_FORMAT);
                    try {
                        diff = System.currentTimeMillis() - format.parse(rslt).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    rsp += diff;
                }
                rsp += ":" + ((rslt2 == null) ? 0 : 1);
                rsp += "^";
            }

            return rsp;
        }

        public String getCurrentUserId(String parkingLotName) {
            final String sql = "SELECT id FROM sure_park_system.tb_reservation "
                    + "INNER JOIN tb_parkinglot ON tb_parkinglot_idx=tb_parkinglot.idx "
                    + "INNER JOIN tb_user ON tb_user_idx=tb_user.idx " + "WHERE name='"
                    + parkingLotName + "'";
            String userId = null;
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                mResultSet.first();
                userId = mResultSet.getString("id");
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
            Log.d(TAG, "getCurrentUserId: " + sql);
            Log.d(TAG, "getCurrentUserId: " + userId);
            return userId;
        }

        public void setParkStatusInfo(String status, String parkingLotName, String charging) {
            final String sql = "UPDATE tb_parkinglot SET status='" + status + "' WHERE name='"
                    + parkingLotName + "'";
            try {
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);
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

        public ArrayList<String> getParkingFacilityInfo() {
            ArrayList<String> parkingName = new ArrayList<String>();
            final String sql = "SELECT mac_addr, name, parkinglot_num FROM tb_controller";
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    parkingName.add(
                            mResultSet.getString("mac_addr") + "^" + mResultSet.getString("name")
                                    + "^" + mResultSet.getString("parkinglot_num"));
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

            return parkingName;
        }

        public ArrayList<String> getParkingLotInfo(String pfName) {
            ArrayList<String> parkingLotInfos = new ArrayList<String>();
            final String sql = "SELECT * FROM tb_parkinglot WHERE name LIKE '" + pfName + "%'";
            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                while (mResultSet.next()) {
                    parkingLotInfos
                            .add(mResultSet.getString("idx") + "^" + mResultSet.getString("name"));
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

            return parkingLotInfos;
        }

        public String[] isValidConfirmationId(String cfId) {
            String[] ret = null;
            final String sql = "SELECT confirm_id, start_time, end_time, name FROM "
                    + "tb_reservation INNER JOIN tb_parkinglot ON tb_parkinglot_idx = "
                    + "tb_parkinglot.idx WHERE confirm_id='" + cfId
                    + "'";/*
                           * + "' AND start_time >= '" +
                           * Utils.getCurrentDateTime(Utils.DATE_FORMAT) + "'";
                           */

            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);
                mResultSet.first();
                ret = new String[] { mResultSet.getString("confirm_id"),
                        mResultSet.getString("start_time"), mResultSet.getString("end_time"),
                        Utils.getParkingLotIdx(mResultSet.getString("name")) };
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

                ret = new UserInformation(id, _firstName, _lastName, _email, _phoneNumber,
                        _credit_card_num, _credit_card_date);

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
         * 
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
         * 
         * @param id
         * @param parkingFacility
         * @param startTime
         * @param endTime
         * @return
         */
        public synchronized String addReservation(String id, String parkingFacility,
                String startTime, String endTime) {

            final String sqlUser = "SELECT idx FROM tb_user WHERE id='" + id + "'";
            ResultSet resevedLotResult = null;
            ResultSet mResultSet3 = null;
            ResultSet mResultSet2 = null;
            ResultSet mResultSet = null;
            try {

                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlUser);
                mResultSet.next();
                String _idx = mResultSet.getString("idx");

                Log.d(TAG, "add reservation info with " + id + ", " + parkingFacility);

                String _confirm_id = TokenGenerator.generateToken();
                Log.d(TAG, "add reservation info confirm id  " + _confirm_id);

                // mResultSet.close();

                final int totalLot = getTotalNumberOfLots(parkingFacility);

                // find empty parking lot at that time
                final String sqlTime = "SELECT tb_parkinglot_idx FROM tb_reservation WHERE (start_time <= '"
                        + startTime + "' AND end_time > '" + startTime + "' ) OR (start_time < '"
                        + endTime + "'  AND end_time >= '" + endTime + "')";
                // final String sqlTime = "SELECT tb_parkinglot_idx FROM
                // tb_reservation WHERE (start_time > '"
                // + endTime + "' AND end_time < '" + startTime + "' )";

                Log.d(TAG, "sql for reserved lots : " + sqlTime);

                ResultSet resultSet = mDataBaseConnectionManager.getStatement()
                        .executeQuery(sqlTime);
                final int reservedTotalLot = resultSet.getFetchSize();
                Log.d(TAG, "reserved lots number : " + reservedTotalLot);

                // check the number of lots
                if (reservedTotalLot >= totalLot) {
                    Log.d(TAG, "out of lots");
                    return null;
                }

                int[] lots = new int[totalLot + 1]; // from 1 to totalLot
                ArrayList<String> parkinglotIdxList = new ArrayList<String>();
                while (resultSet.next()) {
                    parkinglotIdxList.add(resultSet.getString("tb_parkinglot_idx"));
                }

                Iterator<String> it = parkinglotIdxList.iterator();
                while (it.hasNext()) {
                    String parkinglot_idx = (String) it.next();
                    Log.d(TAG, "parkinglot_idx " + parkinglot_idx);
                    String sqlParkingLot = "SELECT name FROM tb_parkinglot WHERE idx='"
                            + parkinglot_idx + "'";

                    mResultSet2 = mDataBaseConnectionManager.getStatement()
                            .executeQuery(sqlParkingLot);
                    mResultSet2.next();
                    String name = mResultSet2.getString("name");
                    // mResultSet2.close();

                    String tt = name.substring(name.length() - 5);

                    Log.d(TAG, "lots name : " + name + " length : " + name.length());
                    int reservedLot = Integer.parseInt(tt);
                    Log.d(TAG, "the number of lots " + reservedLot);
                    // int reservedLot =
                    // Integer.getInteger(name.substring(name.length()-5));
                    lots[reservedLot] = Integer.parseInt(parkinglot_idx); // occupied
                                                                          // lots
                    Log.d(TAG, "lots[reservedLot] " + lots[reservedLot]);

                }

                // get the number of lot in parkingFacility
                int _empty_lot = -1;

                for (int j = 1; j <= totalLot; j++) {
                    if (lots[j] == 0) {
                        Log.d(TAG, "find out empty lot " + j);
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
                Log.d(TAG, "find out idx from tb_parkinglot " + idx);
                String sqlParkingidx = "SELECT idx FROM tb_parkinglot WHERE name='" + idx + "'";
                mResultSet3 = mDataBaseConnectionManager.getStatement().executeQuery(sqlParkingidx);
                mResultSet3.next();

                final String sql = "INSERT into tb_reservation (tb_user_idx, start_time, end_time, tb_parkinglot_idx, confirm_id) VALUES('"
                        + _idx + "', '" + startTime + "', '" + endTime + "', '"
                        + mResultSet3.getString("idx") + "', '" + _confirm_id + "')";

                mDataBaseConnectionManager.getStatement().executeUpdate(sqlCommit);
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);

                lm.log(LogManager.DRIVER, LogManager.BOOKING + "/" + _confirm_id + "/" + id);

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

            final String sql = "SELECT name FROM tb_controller";
            ArrayList<String> ret = new ArrayList<String>();

            try {
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sql);

                while (mResultSet.next()) {
                    ret.add(mResultSet.getString("name"));
                }

                Log.d(TAG, "Get list of facility " + ret.size());

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

        /**
         * insert history with start time
         * 
         * @param startTime
         * @param facility
         * @param parkingLot
         */
        public synchronized void setStartTimeToHistoyTable(String startTime, String confirmID,
                String facility, String parkingLot) {

            try {

                // find out tb_parkinglot_idx
                String idx = String.format("%S%05d", facility, Integer.parseInt(parkingLot));
                Log.d(TAG, "find out idx from tb_parkinglot " + idx);
                String sqlParkingidx = "SELECT idx FROM tb_parkinglot WHERE name='" + idx + "'";
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlParkingidx);
                mResultSet.next();
                final String parkinglot_idx = mResultSet.getString("idx");

                final String sql = "INSERT into tb_history (tb_parkinglot_idx, start_time, confirm_id) VALUES('"
                        + parkinglot_idx + "', '" + startTime + "', '" + confirmID + "' )";

                mDataBaseConnectionManager.getStatement().executeUpdate(sqlCommit);
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);

                lm.log(LogManager.DRIVER, LogManager.PARK + "/" + confirmID + "/" + startTime);

                Log.d(TAG, "insert new history " + parkinglot_idx + " " + startTime);
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
         * set end time into history table
         * 
         * @param endTime
         * @param facility
         * @param parkingLot
         * @return fee
         */
        public synchronized long setEndTimeToHistoyTable(String endTime, String confirmID,
                String facility, String parkingLot) {

            try {

                // find out tb_parkinglot_idx
                final String name = String.format("%S%05d", facility, Integer.parseInt(parkingLot));
                Log.d(TAG, "find out idx from tb_parkinglot " + name);
                final String sqlParkingidx = "SELECT idx FROM tb_parkinglot WHERE name='" + name
                        + "'";
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlParkingidx);
                mResultSet.next();
                final String idx = mResultSet.getString("idx");

                final String sqlStartTime = "SELECT start_time FROM tb_history WHERE tb_parkinglot_idx='"
                        + idx + "' AND confirm_id='" + confirmID + "'";
                mResultSet = mDataBaseConnectionManager.getStatement().executeQuery(sqlStartTime);
                mResultSet.next();
                final String startTime = mResultSet.getString("start_time");

                final long fee = ((ChargeManager) SystemManager.getInstance()
                        .getManager(SystemManager.CHARGE_MANAGER)).calculateCharge(startTime,
                                endTime);
                // UPDATE tb_history SET end_time='2016-10-11 11:11'WHERE
                // idx='1' AND end_time IS NULL
                final String sql = "UPDATE tb_history SET end_time='" + endTime + "', fee='" + fee
                        + "' WHERE tb_parkinglot_idx='" + idx + "' AND confirm_id='" + confirmID
                        + "'";

                mDataBaseConnectionManager.getStatement().executeUpdate(sql);

                // 12:17:50 DELETE FROM tb_reservation WHERE confirm_id='20160623115933HXU1X8'  Error Code: 1175. You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column To disable safe mode, toggle the option in Preferences -> SQL Editor and reconnect. 0.031 sec

                // delete reservation information from reservation table
                final String sqlDelete = "DELETE FROM tb_reservation WHERE confirm_id='" + confirmID
                        + "'";

                mDataBaseConnectionManager.getStatement().executeUpdate(sqlUpdate);
                mDataBaseConnectionManager.getStatement().executeUpdate(sqlDelete);

                lm.log(LogManager.DRIVER,
                        LogManager.LEAVE + "/" + confirmID + "/" + endTime + " / " + fee);
                return fee;

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

            return 0L;
        }

        /**
         * check grace period and remove column on the table
         * 
         * @param gracePeriod
         */
        public synchronized void checkGracePeriod(int gracePeriod) {

            Date date = new Date();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MINUTE, -1 * gracePeriod);
            Date grace = c.getTime();

            final String strGrace = dateFormat.format(grace);

            Log.d(TAG, "Grace period " + strGrace);

            final String sql_delete = "DELETE FROM tb_reservation WHERE start_time < '" + strGrace
                    + "' AND confirm_id NOT IN (SELECT f.confirm_id FROM tb_history f) ";

            try {
                mDataBaseConnectionManager.getStatement().executeUpdate(sqlUpdate);
                int count = mDataBaseConnectionManager.getStatement().executeUpdate(sql_delete);

                if (count != 0) {
                    lm.log(LogManager.DRIVER,
                            LogManager.CANCEL_BOOKING + "/NUM of CANCEL : " + count);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        /**
         * write log message
         * 
         * @param gracePeriod
         */
        public void log(String msg) {

            final String sql = "INSERT into tb_log (message) VALUES('" + msg + "' )";
            try {
                mDataBaseConnectionManager.getStatement().executeUpdate(sql);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}
