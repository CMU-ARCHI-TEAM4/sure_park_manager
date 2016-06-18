package com.lge.sureparkmanager.manager;

public abstract class SystemManagerBase {

    protected boolean mIsRunning = false;

    /**
     * Initialize the manager.
     */
    protected void init() {
        mIsRunning = true;
    }

    /**
     * Check whether a manager is running or stopped.
     * @return
     */
    protected boolean isRunning() {
        return mIsRunning;
    }

    /**
     * Clear the resources related to itself.
     */
    protected void clear() {

    }

    /**
     * Report the death object to the supervisor(?).
     */
    abstract void reportDeath();

}
