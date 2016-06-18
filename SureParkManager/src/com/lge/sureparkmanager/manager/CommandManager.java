package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.exceptions.CommandParserException;
import com.lge.sureparkmanager.utils.Log;

public final class CommandManager extends SystemManagerBase {
    private static final String TAG = CommandManager.class.getSimpleName();

    private static final String COMMAND_SEPARATOR = " ";

    private CommandDispatcher mCommandDispatcher;

    @Override
    protected void init() {
        super.init();

        Log.d(TAG, "init");
        mCommandDispatcher = new CommandDispatcher();
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

        final int first = Integer.parseInt(cmds[0]);
        switch (first) {
            case Commands.CMD_REQ:
                break;
            case Commands.CMD_RES:
                break;
            default:
                break;
        }
        final String macAddr = cmds[1];

        for (int i = 2; i < cmds.length; i++) {
            final String cmd = cmds[i];
            final int c = Integer.parseInt(cmd);
            switch (c) {
                case Commands.CMD_DEVICE_INFO:
                    String mac = cmds[++i];
                    int parkingLotNum = Integer.parseInt(cmds[++i]);
                    mCommandDispatcher.setDeviceInfo(mac, parkingLotNum);
                    break;
                default:
                    break;
            }
        }
    }
}
