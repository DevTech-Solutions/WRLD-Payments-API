package com.nftworlds.wallet.objects.payments;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import lombok.Getter;
import lombok.Setter;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PeerToPeerPayment {

    @Getter
    private static List<PeerToPeerPayment> peerToPeerPayments = new ArrayList<>();

    private final UUID to;
    private final UUID from;
    @Setter private double amount;
    private final Uint256 refid;
    private final Network network;
    private final String reason;
    private final long timeout;

    public PeerToPeerPayment(NFTPlayer to, NFTPlayer from, double amount, Uint256 refid, Network network, String reason, long timeout) {
        this.to = to.getUuid();
        this.from = from.getUuid();
        this.amount = amount;
        this.refid = refid;
        this.network = network;
        this.reason = reason;
        this.timeout = timeout;

        peerToPeerPayments.add(this);
    }

    public static void removePaymentsFor(UUID uuid) {
        peerToPeerPayments.removeIf(peerToPeerPayment -> {
            return peerToPeerPayment.getTo().equals(uuid) || peerToPeerPayment.getFrom().equals(uuid);
        });
    }

    public static PeerToPeerPayment getPayment(Uint256 refid, Network network) {
        for (PeerToPeerPayment p : peerToPeerPayments) {
            if (refid.equals(p.getRefid()) && network == p.getNetwork()) {
                return p;
            }
        }
        return null;
    }

}
