package com.nftworlds.wallet.util;

import com.nftworlds.wallet.NFTWorlds;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * @author LBuke (Teddeh)
 */
public final class LogUtil {

    /**
     * If the debug option is enabled in the config, log the message
     *
     * @param level The level of the log.
     * @param log The message to log
     */
    public static void log(@NotNull Level level, @NotNull String log) {
        if (NFTWorlds.getInstance().getNftConfig().isDebug()) {
            NFTWorlds.getInstance().getLogger().log(level, log);
        }
    }

    /**
     * It logs a message to the console if the debug mode is enabled
     *
     * @param level The level of the log.
     * @param log The message to log.
     */
    public static void log(@NotNull Level level, @NotNull String log, @NotNull Object... args) {
        if (NFTWorlds.getInstance().getNftConfig().isDebug()) {
            NFTWorlds.getInstance().getLogger().log(level, log.formatted(args));
        }
    }

    /**
     * If the debug option is enabled in the config, log the message
     *
     * @param log The message to log.
     */
    public static void log(@NotNull String log) {
        if (NFTWorlds.getInstance().getNftConfig().isDebug()) {
            NFTWorlds.getInstance().getLogger().log(Level.INFO, log);
        }
    }

    /**
     * If the debug flag is set in the config, log the message
     *
     * @param log The message to log.
     */
    public static void log(@NotNull String log, @NotNull Object... args) {
        if (NFTWorlds.getInstance().getNftConfig().isDebug()) {
            NFTWorlds.getInstance().getLogger().log(Level.INFO, log.formatted(args));
        }
    }
}
