package com.lge.sureparkmanager.utils;

import java.util.ArrayList;

public class Utils {

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
}
