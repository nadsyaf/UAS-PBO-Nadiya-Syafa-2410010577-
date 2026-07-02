package com.laundry.util;

import java.text.DecimalFormat;

public class Formatter {

    public static String toRupiah(double value) {
        DecimalFormat df = new DecimalFormat("#,###");
        return "Rp " + df.format(value);
    }
}
