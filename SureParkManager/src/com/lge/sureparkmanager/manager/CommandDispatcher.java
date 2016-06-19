package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

public final class CommandDispatcher {
    private static final String TAG = CommandDispatcher.class.getSimpleName();

    private DataBaseManager mDataBaseManager;

    public CommandDispatcher() {
        Log.d(TAG, "init");
        mDataBaseManager = (DataBaseManager) SystemManager.getInstance()
                .getManager(SystemManager.DATABASE_MANAGER);
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

    public void setParkStatusInfo(String mac, String status, String parkingLotNum,
            String charging) {
        mDataBaseManager.getQueryWrapper().setParkStatusInfo(mac, status, parkingLotNum, charging);
    }
}
