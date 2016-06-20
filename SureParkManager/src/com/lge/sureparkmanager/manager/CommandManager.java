package com.lge.sureparkmanager.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.lge.sureparkmanager.exceptions.CommandParserException;
import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.Utils;

public final class CommandManager extends SystemManagerBase {
    private static final String TAG = CommandManager.class.getSimpleName();

    private CommandDispatcher mCommandDispatcher;
    private HashMap<Integer, CommandListener> mCommandListener =
            new HashMap<Integer, CommandListener>();

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

    public void registerListener(CommandListener commandListener, int cmd) {
        if (!mCommandListener.containsValue(commandListener)) {
            mCommandListener.put(cmd, commandListener);
        }
    }

    public void unregisterListener(CommandListener commandListener) {
        for (Map.Entry<Integer, CommandListener> e : mCommandListener.entrySet()) {
            if (Objects.equals(commandListener, e.getValue())) {
                mCommandListener.remove(e.getKey());
                break;
            }
        }
    }

    public String generateOpenEntryGateCommand(String macAddr) {
        return Commands.CMD_REQ + " " + macAddr + " " + Commands.CMD_ENTRY_GATE +
                " " + Commands.CMD_ENTRY_GATE_OPEN;
    }

    public void parse(String data) {
        if (data == null) {
            throw new CommandParserException("data is null");
        }

        final String[] cmds = data.split(Utils.COMMAND_SEPARATOR);
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
            final String cmd = cmds[2];
            final int c = Integer.parseInt(cmd);
            int subCmd = -1;

            switch (c) {
            case Commands.CMD_DEVICE_INFO: {
                final String mac = cmds[++i];
                final int parkingLotNum = Integer.parseInt(cmds[++i]);
                if (macAddr.equals(mac)) {
                    mCommandDispatcher.setDeviceInfo(macAddr, parkingLotNum);
                }
                break;
            }
            case Commands.CMD_ENTRY_GATE: {
                String status = cmds[++i];
                mCommandDispatcher.setParkEntryGateInfo(macAddr, status);
                break;
            }
            case Commands.CMD_EXIT_GATE: {
                String status = cmds[++i];
                mCommandDispatcher.setParkExitGateInfo(macAddr, status);
                break;
            }
            case Commands.CMD_PARKING: {
                String status = cmds[++i];
                String parkingLotNum = cmds[++i];
                String charging = cmds[++i];
                mCommandDispatcher.setParkStatusInfo(macAddr, status, parkingLotNum, charging);
                break;
            }
            case Commands.CMD_HEART_BIT: {
                @SuppressWarnings("unused")
                String time = cmds[++i];
                AliveCheckerManager acm = (AliveCheckerManager) SystemManager.getInstance()
                        .getManager(SystemManager.ALIVE_CHECKER_MANAGER);
                acm.createAliveChecker(macAddr);
                acm.kick(macAddr, System.currentTimeMillis());
                break;
            }
            case Commands.CMD_ENTRY_SENSOR: {
                @SuppressWarnings("unused")
                int status = subCmd = Integer.parseInt(cmds[++i]);
                break;
            }
            default:
                break;
            }
            // Don't call me, we will call you.
            onCommand(macAddr, c, subCmd);
        }
    }

    private void onCommand(String macAddr, int mainCmd, int subCmd) {
        for (Map.Entry<Integer, CommandListener> e : mCommandListener.entrySet()) {
            if (mainCmd == e.getKey()) {
                e.getValue().onCommand(macAddr, mainCmd, subCmd);
            }
        }
    }

    public interface CommandListener {
        public void onCommand(String macAddr, int mainCmd, int subCmd);
    }
}
