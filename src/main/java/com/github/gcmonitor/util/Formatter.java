package com.github.gcmonitor.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Formatter {

    public static BigDecimal roundToDigits(double value, int digits) {
        return new BigDecimal(value).setScale(digits, RoundingMode.CEILING);
    }

}
