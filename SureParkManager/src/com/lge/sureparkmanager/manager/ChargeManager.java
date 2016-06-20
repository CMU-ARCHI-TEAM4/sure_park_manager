package com.lge.sureparkmanager.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lge.sureparkmanager.db.UserInformation;
import com.lge.sureparkmanager.utils.Log;

/**
 * 
 * @author hakjoo.lee
 *
 */
public class ChargeManager extends SystemManagerBase {

	private static final String TAG = ChargeManager.class.getSimpleName();
	private int PERHOUR = 0;
	private DataBaseManager dbm = (DataBaseManager)SystemManager.getInstance().getManager(
	        SystemManager.DATABASE_MANAGER);
	/**
	 * Initialize the manager.
	 */
	protected void init() {
		super.init();

		try {
			PERHOUR = ((ConfigurationManager) SystemManager.getInstance()
					.getManager(SystemManager.CONFIGURATION_MANAGER)).getConfigtHourlyRate();
		} catch (Exception e) {
			PERHOUR = 10;
		}

	}

	/**
	 * calculate charge
	 * 
	 * @param startDateTime
	 * @param endDateTime
	 * @return amount
	 */
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
			long hour = (min / 60) + (min % 60 >= 30 ? 1 : 0);
			long payment = hour * PERHOUR;

			Log.d(TAG, "parking min : " + min + " hour : " + hour + " payment : " + payment);

			return payment;

		} catch (ParseException e) {

			Log.e(TAG, "Parsing error, date & time should be formated as YYYY-MM-dd HH:MM");

			//e.printStackTrace();
		}

		return 0;
	}

	/**
	 * request charging fee to credit card system
	 * 
	 * @param cardNum
	 * @param cardValid
	 * @param amount
	 */
	public void requestCharge(String cardNum, String cardValid, long amount) {

		StringBuilder message = new StringBuilder();
		message.append("\n\n***********************************************\n");
		message.append("***********************************************\n");
		message.append("REQUEST TO CARD PAYMENT SYSTEM\n");
		message.append("CHARGE FOR PARKING\n");
		message.append("CARD NUMBER :");
		message.append(cardNum +"\n");
		message.append("CARD VALIDATION : ");
		message.append(cardValid+"\n");
		message.append("TOTAL AMOUNT : ");
		message.append(amount+"\n");
		message.append("***********************************************\n");
		message.append("***********************************************\n\n");
		Log.d(TAG, message.toString());

		System.err.println(message);
	}

	@Override
	void reportDeath() {
		// TODO Auto-generated method stub
	}

	/**
	 * get current time for DB
	 * 
	 * @return
	 */
	private String getCurrentTime() {

		Date date = new Date();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String currentDate = dateFormat.format(date);
		
		return currentDate;
	}

	/**
	 * check in to parking facility
	 * 
	 * @param facility ex>"A"
	 * @param parkingLot ex> "1"
	 */
	public void checkIn(String facility, String parkingLot) {
		
		String current = getCurrentTime();
		dbm.getQueryWrapper().setStartTimeToHistoyTable(current, facility, parkingLot);
	}

	/**
	 * check out from parking facility
	 * @param userID ex>"or4nge"
	 * @param facility ex> "A"
	 * @param parkingLot ex> "1"
	 */
	public void checkOut(String userID, String facility, String parkingLot) {
		
		String current = getCurrentTime();
		final long fee = dbm.getQueryWrapper().setEndTimeToHistoyTable(current, facility, parkingLot);

		UserInformation userInfo = dbm.getQueryWrapper().getUserInfomation(userID);
		requestCharge(userInfo.getCreditCardNumber(), userInfo.getCreditCardValidation(), fee);
	}
}
