package com.lge.sureparkmanager.manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.lge.sureparkmanager.utils.Log;

public final class CommandQueue implements Runnable {
    private static final String TAG = CommandQueue.class.getSimpleName();

    private static final long CMD_DELAY = 1000;

    private BlockingQueue<String> mCmdQueue = new LinkedBlockingQueue<String>();

    private CommandManager mCommandManager;
    private boolean mStop = false;

    public CommandQueue() {
        Log.d(TAG, "init");

        mCommandManager = (CommandManager) SystemManager.getInstance().
                getManager(SystemManager.COMMAND_MANAGER);
    }

    public void put(String cmd) {
        try {
            mCmdQueue.put(cmd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeAllCommand() {
        mCmdQueue.clear();
    }

    public void stop() {
        mStop = true;
    }

    @Override
    public void run() {
        while (!mStop) {
            if (mCmdQueue.size() > 0) {
                final String cmd = mCmdQueue.poll();
                Log.d(TAG, "calling parse: " + cmd);
                mCommandManager.parse(cmd);
            } else {
                try {
                    Thread.sleep(CMD_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
