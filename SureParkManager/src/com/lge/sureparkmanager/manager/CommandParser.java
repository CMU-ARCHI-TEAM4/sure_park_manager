package com.lge.sureparkmanager.manager;

import com.lge.sureparkmanager.exceptions.CommandParserException;
import com.lge.sureparkmanager.utils.Log;

public class CommandParser {
    private static final String TAG = CommandParser.class.getSimpleName();

    public static void parse(String data) {
        if (data == null) {
            throw new CommandParserException("data is null");
        }

        final String[] cmds = data.split(" ");
        for (String cmd : cmds) {
            Log.d(TAG, cmd);
        }
    }
}
