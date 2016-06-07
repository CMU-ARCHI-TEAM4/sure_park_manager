package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

public class ParkingFacilityManager extends SystemManagerBase {
    private static final String TAG = ParkingFacilityManager.class.getSimpleName();

    @Override
    protected void init() {
        super.init();

        Log.d(TAG, "init");
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    void reportDeath() {

    }
}
