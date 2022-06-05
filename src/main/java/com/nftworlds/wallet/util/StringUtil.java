package com.nftworlds.wallet.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author LBuke (Teddeh)
 */
public final class StringUtil {

    /**
     * If the string is null or empty, return true.
     *
     * @param string The string to check.
     * @return A boolean value.
     */
    public static boolean isNullOrEmpty(@Nullable String string) {
        return Objects.isNull(string) || string.isEmpty();
    }

    /**
     * If the string is not null and not empty, return true.
     *
     * @param string The string to check.
     * @return A boolean value.
     */
    public static boolean nonNullAndNotEmpty(@Nullable String string) {
        return Objects.nonNull(string) && !string.isEmpty();
    }
}
