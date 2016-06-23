package com.lge.sureparkmanager.manager;

public final class Commands {
    public static final int CMD_REQ = 0;
    public static final int CMD_RES = 1;

    public static final int CMD_DEVICE_INFO = 0;

    public static final int CMD_SERVER_CONFIG = 1;
    public static final int CMD_HEART_BIT = 2;

    public static final int CMD_ENTRY_SENSOR = 3;
    public static final int CMD_ENTRY_SENSOR_DET = 0;
    public static final int CMD_ENTRY_SENSOR_NOTDET = 1;

    public static final int CMD_CONFIRMATION = 4;

    public static final int CMD_ENTRY_GATE = 5;
    public static final int CMD_ENTRY_GATE_OPEN = 0;
    public static final int CMD_ENTRY_GATE_CLOSE = 1;

    public static final int CMD_PARKING = 6;
    public static final int CMD_PARKING_IN = 1;
    public static final int CMD_PARKING_OUT = 0;

    public static final int CMD_EXIT_GATE = 7;
    public static final int CMD_EXIT_GATE_OPEN = 0;
    public static final int CMD_EXIT_GATE_CLOSE = 1;

    public static final int CMD_EXIT_SENSOR = 8;
    public static final int CMD_EXIT_SENSOR_DET = 0;
    public static final int CMD_EXIT_SENSOR_NOTDET = 1;

    public static final int CMD_TIME_SYNC = 9;

    public static String getCmdName(int cmd) {
        switch (cmd) {
        case CMD_DEVICE_INFO: {
            return "DEVICE_INFO";
        }
        case CMD_SERVER_CONFIG: {
            return "SERVER_CONFIG";
        }
        case CMD_HEART_BIT: {
            return "HEART_BIT";
        }
        case CMD_ENTRY_SENSOR: {
            return "ENTRY_SENSOR";
        }
        case CMD_CONFIRMATION: {
            return "CONFIRMATION";
        }
        case CMD_ENTRY_GATE: {
            return "ENTRY_GATE";
        }
        case CMD_PARKING: {
            return "PARKING";
        }
        case CMD_EXIT_GATE: {
            return "EXIT_GATE";
        }
        case CMD_EXIT_SENSOR: {
            return "EXIT_SENSOR";
        }
        case CMD_TIME_SYNC: {
            return "TIME_SYNC";
        }
        }

        return "";
    }
}
