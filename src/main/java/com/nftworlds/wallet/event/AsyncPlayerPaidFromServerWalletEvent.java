package com.nftworlds.wallet.event;

import com.nftworlds.wallet.objects.Network;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class AsyncPlayerPaidFromServerWalletEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final OfflinePlayer receiver;
    private final double amount;
    private final Network network;
    private final String reason;
    private final String receiptLink;

    @Setter
    private boolean defaultReceiveMessage;

    public AsyncPlayerPaidFromServerWalletEvent(@NotNull Player receiver, double amount, @NotNull Network network,
                                                @NotNull String reason, @NotNull String receiptLink) {
        super(true);
        this.receiver = receiver;
        this.amount = amount;
        this.network = network;
        this.reason = reason;
        this.receiptLink = receiptLink;
        this.defaultReceiveMessage = true;
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
