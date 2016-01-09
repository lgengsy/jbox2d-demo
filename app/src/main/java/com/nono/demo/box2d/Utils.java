package com.nono.demo.box2d;

public class Utils {
    public static int DpToPx(float x) {
        int result = 0;
        final float scale = LibApplication.getInstance().getResources().getDisplayMetrics().density;
        result = (int) (x * scale + 0.5f);
        return result;
    }


}
