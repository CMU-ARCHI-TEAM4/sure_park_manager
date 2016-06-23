/**
 * 
 */
package com.lge.sureparkmanager.manager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hakjoo.lee
 *
 */
public final class LogManager extends SystemManagerBase {

    // private static final String TAG = ChargeManager.class.getSimpleName();
    private DataBaseManager dbm = null;

    public final static String ATTENDANT = "ATTEDANT";
    public final static String OWNER = "OWNER";
    public final static String DRIVER = "DRIVER";

    public final static String LOGIN = "LOGIN";
    public final static String LOGOUT = "LOGOUT";
    public final static String BOOKING = "BOOKING";
    public final static String CANCEL_BOOKING = "CANCEL_BOOKING";
    public final static String PAYMENT = "PAYMENT";
    public final static String PARK = "PARK";
    public final static String LEAVE = "LEAVE";
    public final static String UNATHORIZED_LOGIN_DETECTED = "UNATHORIZED_LOGIN_DETECTED";

    /*
     * (non-Javadoc)
     * 
     * @see com.lge.sureparkmanager.manager.SystemManagerBase#reportDeath()
     */
    @Override
    void reportDeath() {

    }

    public void log(String who, String activity) {

        if (dbm == null) {
            dbm = (DataBaseManager) SystemManager.getInstance()
                    .getManager(SystemManager.DATABASE_MANAGER);
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date date = new Date();
        final String current = format.format(date);

        StringBuilder msg = new StringBuilder();
        msg.append("[");
        msg.append(current);
        msg.append("]");
        msg.append("[");
        msg.append(who);
        msg.append("]");
        msg.append(activity);

        dbm.getQueryWrapper().log(msg.toString());

    }
}
