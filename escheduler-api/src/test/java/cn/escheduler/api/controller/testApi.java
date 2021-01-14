package cn.escheduler.api.controller;

import java.util.HashSet;
import java.util.Set;

public class testApi {

    public static void main(String[] args) {

        Set<String> sets = new HashSet<String>();
        sets.add("1");
        sets.add("2");
        sets.remove("1");
        sets.remove("2");
        System.out.println(sets.toString());

    }
}
