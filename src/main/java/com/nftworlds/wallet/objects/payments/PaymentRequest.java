package com.nftworlds.wallet.objects.payments;

import com.nftworlds.wallet.event.PlayerTransactEvent;
import com.nftworlds.wallet.objects.Network;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class PaymentRequest<T> {

    @Getter
    private static final List<PaymentRequest<?>> paymentRequests = new ArrayList<>();

    private final UUID associatedPlayer;
    private final double amount;
    private final Uint256 refid;
    private final Network network;
    private final String reason;
    private final long timeout;
    private final boolean canDuplicate;
    private final T payload;

    public PaymentRequest(UUID associatedPlayer, double amount, Uint256 refid, Network network, String reason, long timeout, boolean canDuplicate, T payload) {
        this.associatedPlayer = associatedPlayer;
        this.amount = amount;
        this.refid = refid;
        this.network = network;
        this.reason = reason;
        this.timeout = timeout;
        this.canDuplicate = canDuplicate;
        this.payload = payload;
        paymentRequests.add(this);
    }

    public void finalizeTransaction() {
        PlayerTransactEvent<?> event = new PlayerTransactEvent<>(
                Objects.requireNonNull(Bukkit.getPlayer(this.getAssociatedPlayer())),
                this.getAmount(), this.getReason(), this.getRefid(), this.getPayload());

        event.callEvent();

        PaymentRequest.getPaymentRequests().remove(this);
    }

    public static void removePaymentsFor(UUID uuid) {
        paymentRequests.removeIf(paymentRequest -> paymentRequest.getAssociatedPlayer().equals(uuid));
    }

    public static PaymentRequest<?> getPayment(Uint256 refid, Network network) {
        for (PaymentRequest<?> p : paymentRequests) {
            if (refid.equals(p.getRefid()) && network == p.getNetwork()) {
                return p;
            }
        }
        return null;
    }

}
