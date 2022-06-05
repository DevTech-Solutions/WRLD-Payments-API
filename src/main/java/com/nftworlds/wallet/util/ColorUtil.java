package com.nftworlds.wallet.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class ColorUtil {

    /**
     * It takes a string, and returns a string
     *
     * @param string The string you want to color.
     * @return The string with the color codes translated.
     */
    public static @NotNull String rgb(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
