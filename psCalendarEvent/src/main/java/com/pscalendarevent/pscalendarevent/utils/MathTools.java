package com.pscalendarevent.pscalendarevent.utils;

public class MathTools {
    public static int floorMod(int value, int mod) {
        int ret = value%mod;
        if (ret < 0) ret += mod;
        return ret;
    }
}