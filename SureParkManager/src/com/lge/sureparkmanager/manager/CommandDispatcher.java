package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

public class CommandDispatcher {
    private static final String TAG = CommandDispatcher.class.getSimpleName();

    private DataBaseManager mDataBaseManager;

    public CommandDispatcher() {
        Log.d(TAG, "init");
        mDataBaseManager = (DataBaseManager) SystemManager.getInstance().
                getManager(SystemManager.DATABASE_MANAGER);
    }

    public void setDeviceInfo(String mac, int parkingLotNum) {
        mDataBaseManager.getQueryWrapper().setParkInfo(mac, parkingLotNum);
    }
}
