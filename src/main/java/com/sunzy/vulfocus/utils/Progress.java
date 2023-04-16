package com.sunzy.vulfocus.utils;

import com.sunzy.vulfocus.model.ProgrossInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Progress {
    public static Map<String, ProgrossInfo> map = new HashMap<>();

    private static final String ANSI_CLEAR_LINE = "\033[2K";
    private static final String ANSI_HOME = "\033[H";
    private static final String ANSI_SAVE_CURSOR = "\033[s";
    private static final String ANSI_RESTORE_CURSOR = "\033[u";


    public static void main(String[] args) {
//        testprint();
        ProgrossInfo progrossInfo = new ProgrossInfo();
        ProgrossInfo progrossInfo1 = new ProgrossInfo();
        ProgrossInfo progrossInfo2 = new ProgrossInfo();
        progrossInfo.setId("1");
        progrossInfo.setProgress(10);
        progrossInfo1.setId("2");
        progrossInfo1.setProgress(10);
        progrossInfo2.setId("3");
        progrossInfo2.setProgress(10);
        map.put(progrossInfo.getId(), progrossInfo);
        map.put(progrossInfo1.getId(), progrossInfo1);
        map.put(progrossInfo2.getId(), progrossInfo2);
        printProgress();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progrossInfo.setId("1");
        progrossInfo.setProgress(20);
        progrossInfo1.setId("2");
        progrossInfo1.setProgress(30);
        progrossInfo2.setId("3");
        progrossInfo2.setProgress(40);
        map.put(progrossInfo.getId(), progrossInfo);
        map.put(progrossInfo1.getId(), progrossInfo1);
        map.put(progrossInfo2.getId(), progrossInfo2);
        System.out.print(ANSI_HOME);
        printProgress();
    }

    public static void testprint(){
        System.out.print("11231321");
        System.out.print("\r2222222");
    }

    public static void printProgress(){
        Set<Map.Entry<String, ProgrossInfo>> entries = map.entrySet();
        StringBuffer progross = new StringBuffer();

        int index = entries.size();
        for (Map.Entry<String, ProgrossInfo> entry : entries) {
            index--;
            if(index > 0){
                progross.append(entry.getValue()).append("\n");
            } else {
                progross.append(entry.getValue());
            }
        }
//        progross.append("\r\r\r ");
        System.out.print(progross.toString());
    }
}
