package com.nftworlds.wallet.objects;

import com.nftworlds.wallet.NFTWorlds;
import com.nftworlds.wallet.contracts.nftworlds.Players;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NFTPlayer {
    private static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";

    private static final ConcurrentHashMap<UUID, NFTPlayer> players = new ConcurrentHashMap<>();

    private final UUID uuid;
    private final List<Wallet> wallets;
    private boolean linked = false;

    public NFTPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;

        final Players playerContract = NFTWorlds.getInstance().getPlayers();

        String primary = null;
        try {
            primary = playerContract.getPlayerPrimaryWallet(uuid.toString().replace("-", ""));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        List<String> secondary = null;
        try {
            secondary = playerContract.getPlayerSecondaryWallets(uuid.toString().replace("-", ""));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        this.linked = !primary.equalsIgnoreCase(EMPTY_ADDRESS);

        this.wallets = new ArrayList<>();
        this.wallets.add(new Wallet(this, primary));
        for (String wallet : secondary) {
            this.wallets.add(new Wallet(this, wallet));
        }

        players.put(uuid, this);
    }

    /**
     * Get a player's wallet(s)
     *
     * @return player's wallet(s)
     */
    public @NotNull List<Wallet> getWallets() {
        return wallets;
    }

    /**
     * Get a player's primary wallet
     *
     * @return player's wallet
     */
    public @NotNull Wallet getPrimaryWallet() {
        return wallets.get(0);
    }

    /**
     * If the wallet is not empty, then set the wallet to the first position in the list of wallets, and remove the
     * previous wallet from the list of wallets.
     *
     * @param wallet The wallet to set as the primary wallet.
     */
    public void setPrimaryWallet(@NotNull Wallet wallet) {
        final Wallet previousWallet = wallets.set(0, wallet);
        if (previousWallet != null) {
            NFTWorlds.getInstance().removeWallet(previousWallet);
        }
        linked = !wallet.getAddress().equalsIgnoreCase(EMPTY_ADDRESS);
    }

    /**
     * It creates a QR code that the player can scan with their phone to pay the server (if using bedrock platform)
     *
     * @param amount The amount of WRLD to request.
     * @param network The network to request payment on.
     * @param reason The reason for the payment request. This will be displayed to the player.
     * @param canDuplicate If true, the player can pay multiple times. If false, the player can only pay once.
     * @param payload This is a generic object that can be used to pass data to the callback.
     */
    public <T> void requestWRLD(double amount, @NotNull Network network, @NotNull String reason,
                                boolean canDuplicate, @NotNull T payload) throws IOException, InterruptedException {
        getPrimaryWallet().requestWRLD(amount, network, reason, canDuplicate, payload);
    }

    /**
     * It sends a transaction to the player's wallet
     *
     * @param amount The amount of WRLD to send.
     * @param network The network to send the payment to. Currently only Polygon is supported.
     * @param reason The reason for the payment. This will be displayed to the player.
     */
    public void sendWRLD(double amount, @NotNull Network network, @NotNull String reason) {
        getPrimaryWallet().payWRLD(amount, network, reason);
    }

    /**
     * This function creates a new payment request for the player, and sends them a link to the payment page
     *
     * @param to The player to send the payment to
     * @param amount The amount of NFTs to send.
     * @param network The network you want to use.
     * @param reason The reason for the payment.
     *               This will be displayed to the user when they are asked to approve the payment.
     */
    public void createPlayerPayment(@NotNull NFTPlayer to, double amount, @NotNull Network network, @NotNull String reason) {
        getPrimaryWallet().createPlayerPayment(to, amount, network, reason);
    }

    public static void remove(UUID uuid) {
        final NFTPlayer player = players.remove(uuid);
        if (player != null) {
            final NFTWorlds plugin = NFTWorlds.getInstance();
            for (Wallet wallet : player.wallets) {
                plugin.removeWallet(wallet);
            }
        }
    }

    /**
     * Check if player has their wallet linked
     *
     * @return if player has wallet linked
     */
    public boolean isLinked() {
        return linked;
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * Get the NFTPlayer object from the players HashMap by the UUID.
     *
     * @param uuid The UUID of the player you want to get the NFTPlayer object of.
     * @return The NFTPlayer object associated with the UUID.
     */
    public static NFTPlayer getByUUID(UUID uuid) {
        return players.get(uuid);
    }

    public static @NotNull ConcurrentHashMap<UUID, NFTPlayer> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final NFTPlayer player = (NFTPlayer) object;
        return Objects.equals(uuid, player.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
