package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.exceptions.CommandParserException;
import com.lge.sureparkmanager.utils.Log;

public final class CommandManager extends SystemManagerBase {
    private static final String TAG = CommandManager.class.getSimpleName();

    private static final String COMMAND_SEPARATOR = " ";

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
    void reportDeath() {

    }

    public void parse(String data) {
        if (data == null) {
            throw new CommandParserException("data is null");
        }

        final String[] cmds = data.split(COMMAND_SEPARATOR);
        for (String cmd : cmds) {
            Log.d(TAG, cmd);
        }
    }
}
