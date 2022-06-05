package com.nftworlds.wallet.util;

import net.md_5.bungee.api.ChatColor;

public class ColorUtil {

    public static String rgb(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
