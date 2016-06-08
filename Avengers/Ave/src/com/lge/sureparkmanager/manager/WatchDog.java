package com.lge.sureparkmanager.manager;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.lge.sureparkmanager.utils.Assert;
import com.lge.sureparkmanager.utils.Log;

public class WatchDog implements Runnable {
    private static final String TAG = WatchDog.class.getSimpleName();

    private Thread mThread;
    private Semaphore mSemaphore;
    private volatile boolean mStopRequested;
    private final long mTimeoutInMilliSecs;
    private TimeoutCallback mCallback = null;

    public WatchDog(long timeoutInMilliSecs) {
        mTimeoutInMilliSecs = timeoutInMilliSecs;
    }

    public WatchDog(long timeoutInMilliSecs, TimeoutCallback callback) {
        this(timeoutInMilliSecs);
        mCallback = callback;
    }

    public void start() {
        Log.d(TAG, "start");
        mStopRequested = false;
        mSemaphore = new Semaphore(0);
        mThread = new Thread(this);
        mThread.start();
    }

    public void stop() {
        Log.d(TAG, "stop");
        if (mThread == null) {
            return; // already finished
        }
        mStopRequested = true;
        mSemaphore.release();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // ignore
        }
        mThread = null;
        mSemaphore = null;
    }

    public void reset() {
        if (!mStopRequested) {
            mSemaphore.release();
        }
    }

    @Override
    public void run() {
        while (!mStopRequested) {
            try {
                boolean success = mSemaphore.tryAcquire(mTimeoutInMilliSecs, TimeUnit.MILLISECONDS);
                if (mCallback == null) {
                    Assert.assertTrue("Watchdog timed-out", success);
                } else if (!success) {
                    mCallback.onTimeout();
                }
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * Called by the Watchdog when it has timed out.
     */
    public interface TimeoutCallback {
        public void onTimeout();
    }
}
