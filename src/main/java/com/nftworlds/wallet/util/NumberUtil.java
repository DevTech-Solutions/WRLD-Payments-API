package com.nftworlds.wallet.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    /**
     * Round a double to a specified number of decimal places using a half-up rounding strategy.
     *
     * @param value The value to be rounded.
     * @param places The number of decimal places to round to.
     * @return A double value rounded to the nearest whole number.
     */
    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
