package com.nftworlds.wallet.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * @author LBuke (Teddeh)
 */
public final class PlayerUtil {

    /**
     * If the player is online, return their name, otherwise return their UUID
     *
     * @param uuid The UUID of the player you want to get the name of.
     * @return The name of the player.
     */
    public static @NotNull String getName(@NotNull UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);
        if (Objects.nonNull(player) && player.isOnline()) {
            return player.getName();
        }

        return uuid.toString();
    }

    /**
     * Send a message to a player if they are online.
     *
     * @param uuid The UUID of the player you want to send the message to.
     * @param string The string to send to the player.
     */
    public static void sendMessage(@NotNull UUID uuid, @NotNull String string) {
        final Player player = Bukkit.getPlayer(uuid);
        if (Objects.nonNull(player) && player.isOnline()) {
            player.sendMessage(Component.text(string));
        }
    }

    /**
     * It sends an action bar to a player
     *
     * @param uuid The UUID of the player you want to send the action bar to.
     * @param string The string to send to the player.
     */
    public static void sendActionBar(@NotNull UUID uuid, @NotNull String string) {
        final Player player = Bukkit.getPlayer(uuid);
        if (Objects.nonNull(player) && player.isOnline()) {
            player.sendActionBar(Component.text(string));
        }
    }
}
