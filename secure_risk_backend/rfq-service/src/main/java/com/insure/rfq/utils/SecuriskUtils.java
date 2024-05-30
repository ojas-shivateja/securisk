package com.insure.rfq.utils;

import java.util.List;


public class SecuriskUtils {

    private SecuriskUtils() {
    }

    public static boolean checkDuplicateValues(List<String> list) {
        List<String> list2 = list.stream().distinct().toList();
        return list.size() == list2.size();
    }

}
