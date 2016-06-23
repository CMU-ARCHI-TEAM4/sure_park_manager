package com.lge.sureparkmanager.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class Utils {
    public static final String COMMAND_SEPARATOR = " ";
    public static final String COMMAND_LAST_CHAR = ">";
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static String DEVICE_SYNC_TIME_FORMAT = "yyyy-MM-dd-HH-mm-ss";

    public static String getNextControllerName(String curName) {
        final char[] curNameCh = curName.toCharArray();
        final int len = curNameCh.length;

        boolean nextInc = true;
        StringBuilder sb = new StringBuilder();
        for (int i = len - 1; i >= 0; i--) {
            char ch = curNameCh[i];
            if (ch == 'Z' && nextInc) {
                sb.insert(0, 'A');
            } else {
                int c;
                if (nextInc) {
                    c = ch + 1;
                    nextInc = false;
                } else {
                    c = ch;
                }
                sb.insert(0, (char) c);
            }
        }

        if (nextInc) {
            sb.insert(0, 'A');
        }

        return sb.toString();
    }

    public static ArrayList<String> generateParkingLotNum(int parkingLotNum) {
        ArrayList<String> parkingLotNumList = new ArrayList<String>();
        for (int i = 1; i <= parkingLotNum; i++) {
            parkingLotNumList.add(String.format("%05d", i));
        }

        return parkingLotNumList;
    }

    public static String getParkingLotName(String pfn, String parkingLotIdx) {
        return pfn + String.format("%05d", Integer.parseInt(parkingLotIdx));
    }

    public static String getParkingLotIdx(String parkingLotName) {
        if (parkingLotName == null) {
            return null;
        }

        return parkingLotName.substring(parkingLotName.lastIndexOf("0") + 1);
    }

    public static String getParkingFacilityName(String parkingLotName) {
        if (parkingLotName == null) {
            return null;
        }

        return parkingLotName.substring(0, parkingLotName.indexOf("0"));
    }

    public static String getCurrentDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getFirstTime(String year, String month) {
        String lastTime = String.format("%s-%02d-%s %s", year, Integer.parseInt(month), "01",
                "00:00");
        return lastTime;
    }

    public static String getLastTime(String year, String month) {
        String lastTime = String.format("%s-%02d-%s %s", year, Integer.parseInt(month),
                getLastDay(year, month), "24:00");
        return lastTime;
    }

    public static String getLastDay(String year, String month) {
        GregorianCalendar calendar = new GregorianCalendar();

        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month) - 1;

        calendar.set(yearInt, monthInt, 1);

        int dayInt = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return Integer.toString(dayInt);
    }

    public static String generateColor(Random r) {
        final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        char[] s = new char[7];
        int n = r.nextInt(0x1000000);

        s[0] = '#';
        for (int i = 1; i < 7; i++) {
            s[i] = hex[n & 0xf];
            n >>= 4;
        }
        return new String(s);
    }

    public static String getCurrentTimeSubMins(int sub) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -(sub));
        return df.format(cal.getTime());
    }
}
