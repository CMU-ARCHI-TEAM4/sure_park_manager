package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.Utils;

public final class CommandDispatcher {
    private static final String TAG = CommandDispatcher.class.getSimpleName();

    private DataBaseManager mDataBaseManager;
    private ChargeManager mChargeManager;

    public CommandDispatcher() {
        Log.d(TAG, "init");
        mDataBaseManager = (DataBaseManager) SystemManager.getInstance()
                .getManager(SystemManager.DATABASE_MANAGER);
        mChargeManager = (ChargeManager) SystemManager.getInstance()
                .getManager(SystemManager.CHARGE_MANAGER);
    }

    public void setDeviceInfo(String mac, int parkingLotNum) {
        mDataBaseManager.getQueryWrapper().setParkInfo(mac, parkingLotNum);
    }

    public void setParkEntryGateInfo(String mac, String status) {
        mDataBaseManager.getQueryWrapper().setParkEntryGateInfo(mac, status);
    }

    public void setParkExitGateInfo(String mac, String status) {
        mDataBaseManager.getQueryWrapper().setParkExitGateInfo(mac, status);
    }

    public void setParkStatusInfo(String mac, String status, String parkingLotNum, String charging,
            String confirmId) {
        int st = Integer.parseInt(status);
        final String parkingFacilityName = mDataBaseManager.getQueryWrapper()
                .getParkingFacilityName(mac);
        final String parkingLotName = Utils.getParkingLotName(parkingFacilityName, parkingLotNum);

        if (st == Commands.CMD_PARKING_IN) {
            Log.d(TAG, "mChargeManager: " + mChargeManager);
            mChargeManager.checkIn(parkingFacilityName, parkingLotNum);
        } else {
            mChargeManager.checkOut(
                    mDataBaseManager.getQueryWrapper().getCurrentUserId(parkingLotNum), parkingFacilityName,
                    parkingLotNum);
        }

        mDataBaseManager.getQueryWrapper().setParkStatusInfo(status, parkingLotName, charging);
    }
}
