package com.nftworlds.wallet.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.web3j.abi.datatypes.generated.Uint256;

/**
 * Holds information for peer to peer transaction events
 */
public class PeerToPeerPayEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player to;
    private final Player from;
    private final double amount;
    private final String reason;
    private final Uint256 refID;

    public PeerToPeerPayEvent(@NotNull Player to, @NotNull Player from, double amount,
                              @NotNull String reason, @NotNull Uint256 refID) {
        this.to = to;
        this.from = from;
        this.amount = amount;
        this.reason = reason;
        this.refID = refID;
    }

    /**
     * Gets the player the payment was sent to
     *
     * @return Receiver of transaction
     */
    @NotNull
    public Player getTo() { return to; }

    /**
     * Gets the player the payment was sent by
     *
     * @return Sender of transaction
     */
    @NotNull
    public Player getFrom() { return from; }

    /**
     * Gets the amount of $WRLD from the transaction
     *
     * @return Amount of $WRLD
     */
    @NotNull
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the amount of $WRLD from the transaction
     *
     * @return Amount of $WRLD
     */
    @NotNull
    public double getWRLD() {
        return amount;
    }

    /**
     * Gets the reason for the transaction
     *
     * @return Transaction Reason
     */
    @NotNull
    public String getReason() {
        return reason;
    }

    /**
     * Gets the refid for the transaction
     *
     * @return Payment reference ID
     */
    @NotNull
    public Uint256 getRefID() {
        return refID;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
