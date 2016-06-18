package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.utils.Log;

public final class ConfigurationManager extends SystemManagerBase {
    private static final String TAG = ConfigurationManager.class.getSimpleName();

    private DataBaseManager mDataBaseManager;

    @Override
    protected void init() {
        super.init();

        Log.d(TAG, "init");

        mDataBaseManager = (DataBaseManager) SystemManager.getInstance().
                getManager(SystemManager.DATABASE_MANAGER);
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
    protected void reportDeath() {

    }

    public int getConfigSocketPortNum() {
        return mDataBaseManager.getQueryWrapper().getSocketPortNum();
    }

    public int getConfigGracePeriodTime() {
        return mDataBaseManager.getQueryWrapper().getGracePeriodTime();
    }

    public int getConfigtHourlyRate() {
        return mDataBaseManager.getQueryWrapper().getHourlyRate();
    }
}
