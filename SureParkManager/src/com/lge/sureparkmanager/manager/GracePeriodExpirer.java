package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

/**
 * 
 * @author hakjoo.lee
 *
 */
public final class GracePeriodExpirer extends SystemManagerBase {

	private final static int PERIOD = 60 * 1000 ;
	
	private static final String TAG = ChargeManager.class.getSimpleName();
	private DataBaseManager dbm = (DataBaseManager)SystemManager.getInstance().getManager(
	        SystemManager.DATABASE_MANAGER);
	
	/**
	 * Initialize the manager.
	 */
	protected void init() {
		super.init();
		(new CheckerThread()).start();
	}
	
	@Override
	void reportDeath() {
		
	}
	
	

	private class CheckerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                
            	// check db
            	// Log.d(TAG, "GracePeriodExpirer is running");
            	int GRACE_PERIOD = ((ConfigurationManager)(SystemManager.getInstance().getManager(SystemManager.CONFIGURATION_MANAGER))).getConfigGracePeriodTime();
                
            	dbm.getQueryWrapper().checkGracePeriod(GRACE_PERIOD);
                try {
                    Thread.sleep(PERIOD);
                } catch (InterruptedException e1) {

                }
            }
        }
    }
}
