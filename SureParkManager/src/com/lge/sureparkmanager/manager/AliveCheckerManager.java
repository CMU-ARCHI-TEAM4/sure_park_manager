package com.lge.sureparkmanager.manager;

import java.util.HashMap;
import java.util.Map;

import com.lge.sureparkmanager.utils.Log;

public final class AliveCheckerManager extends SystemManagerBase {
    private static final String TAG = AliveCheckerManager.class.getSimpleName();

    private static final int ALIVE_CHECKER_TIMEOUT = 30 * 1000;
    
    private HashMap<String, AliveChecker> mAliveCheckers = new HashMap<String, AliveChecker>();

    @Override
    protected void init() {
        super.init();
        Log.d(TAG, "init");
        (new AliveCheckerThread()).start();
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

    public void createAliveChecker(String mac) {
        if (mAliveCheckers.get(mac) == null) {
            AliveChecker aliveChecker = new AliveChecker();
            mAliveCheckers.put(mac, aliveChecker);
        }
    }

    public void kick(String mac, long time) {
        AliveChecker ac = mAliveCheckers.get(mac);
        if (ac != null) {
            ac.kick(time);
        }
    }

    public boolean isAlive(String mac) {
        AliveChecker ac = mAliveCheckers.get(mac);
        if (ac != null) {
            return ac.isAlive();
        }
        return false;
    }

    private class AliveCheckerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                for (Map.Entry<String, AliveChecker> e : mAliveCheckers.entrySet()) {
                    final AliveChecker ac = e.getValue();
                    final long now = System.currentTimeMillis();
                    if ((now - ac.getKickTime()) >= ALIVE_CHECKER_TIMEOUT) {
                        ac.reset(now);
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {

                }
            }
        }
    }
}
