package com.lge.sureparkmanager.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static DateFormat mDateFormat = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm:ss.SSS");

    private static String getFormatDate() {
        return "[" + mDateFormat.format(new Date()) + "] ";
    }

    public static void d(final String tag, final String message) {
        System.out.println(getFormatDate() + "[" + tag + "] " + message);
    }

    public static void d(final String tag, final String message, final Throwable t) {
        System.out.println(getFormatDate() + "[" + tag + "] " + message + " : " + t);
    }

    public static void e(final String tag, final String message) {
        d(tag, message);
    }

    public static void e(final String tag, final String message, final Throwable t) {
        d(tag, message, t);
    }

    public static void w(final String tag, final String message) {
        d(tag, message);
    }

    public static void w(final String tag, final String message, final Throwable t) {
        d(tag, message, t);
    }
}
