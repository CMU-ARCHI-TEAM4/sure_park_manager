package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

public final class ParkingFacilityManager extends SystemManagerBase {
    private static final String TAG = ParkingFacilityManager.class.getSimpleName();

    @Override
    protected void init() {
        super.init();
        Log.d(TAG, "init");
    }

    @Override
    protected boolean isRunning() {
        return super.isRunning();
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    void reportDeath() {

    }
}
