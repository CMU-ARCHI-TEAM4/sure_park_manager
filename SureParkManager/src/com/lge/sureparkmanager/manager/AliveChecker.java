package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

public final class AliveChecker {
    private static final String TAG = AliveChecker.class.getSimpleName();

    private boolean mControllerAlive = false;
    private long mKickTime;

    public AliveChecker() {
        Log.d(TAG, "init");
        mKickTime = System.currentTimeMillis();
    }

    public void kick(long time) {
        mControllerAlive = true;
        mKickTime = time;
    }

    public void reset(long now) {
        mControllerAlive = false;
        mKickTime = now;
    }

    public boolean isAlive() {
        return mControllerAlive;
    }

    public long getKickTime() {
        return mKickTime;
    }
}
