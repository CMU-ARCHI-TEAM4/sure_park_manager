package com.lge.sureparkmanager.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.lge.sureparkmanager.utils.Log;

public class ChargeManager extends SystemManagerBase {

	private static final String TAG = ChargeManager.class.getSimpleName();
	private int PERHOUR = 0;
	
	/**
     * Initialize the manager.
     */
    protected void init() {
        super.init();
        
        try {
        	PERHOUR = ((ConfigurationManager)SystemManager.getInstance().getManager(SystemManager.CONFIGURATION_MANAGER)).getConfigtHourlyRate();
        } catch(Exception e) {
        	PERHOUR = 10;
        }
        
    }
    
	public long calculateCharge(String startDateTime, String endDateTime) {
		try {
			
			if (PERHOUR == 0) {
				init();
			}
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date startDate = format.parse(startDateTime);
			Date endDate = format.parse(endDateTime);

			long period = endDate.getTime() - startDate.getTime();
			if (period <= 0) {
				// ERROR, start time is bigger than end time
				Log.e(TAG, "Error!! end time is bigger than start time");
				return -1;
			}

			long min = period / (1000 * 60);
			long hour = (min / 60) + (min % 60 > 30 ? 1: 0);
			long payment = hour * PERHOUR;
			
			Log.d(TAG, "parking min : " + min + " hour : " + hour + "payment : " + payment);
			
			return payment;

		} catch (ParseException e) {

			e.printStackTrace();
		}

		return 0;
	}

	public void checkout(String cardNum, String cardValid, long amount) {
		System.err.println("***********************************************");
		System.err.println("***********************************************");
		StringBuilder message = new StringBuilder();
		message.append("CHARGE FOR PARKING");
		message.append("CARD NUMBER :");
		message.append(cardNum);
		message.append("CARD VALIDATION : ");
		message.append(cardValid);
		message.append("TOTAL AMOUNT : ");
		message.append(amount);
		Log.d(TAG, message.toString());
		System.err.println("* REQUEST TO CARD PAYMENT SYSTEM ");
		System.err.println(message);
		System.err.println("***********************************************");
		System.err.println("***********************************************");
	}

	@Override
	void reportDeath() {
		// TODO Auto-generated method stub

	}
}
