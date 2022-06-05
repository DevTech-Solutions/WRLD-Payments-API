package com.nftworlds.wallet.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.web3j.abi.datatypes.generated.Uint256;

/**
 * Holds information for player transaction events
 */
public class PlayerTransactEvent<T> extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final double amount;
    private final String reason;
    private final Uint256 refID;
    private final T payload;

    public PlayerTransactEvent(@NotNull Player player, double amount, @NotNull String reason,
                               @NotNull Uint256 refID, @NotNull T payload) {
        super(player);
        this.amount = amount;
        this.reason = reason;
        this.refID = refID;
        this.payload = payload;
    }

    /**
     * Gets the amount of $WRLD from the transaction
     *
     * @return Amount of $WRLD
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the amount of $WRLD from the transaction
     *
     * @return Amount of $WRLD
     */
    public double getWRLD() {
        return amount;
    }

    /**
     * Gets the reason for the transaction
     *
     * @return Transaction Reason
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Gets the refid for the transaction
     *
     * @return Payment reference ID
     */
    public @NotNull Uint256 getRefID() {
        return refID;
    }

    public @NotNull T getPayload() {
        return payload;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
