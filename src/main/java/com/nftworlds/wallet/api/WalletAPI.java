package com.nftworlds.wallet.api;

import com.nftworlds.wallet.NFTWorlds;
import com.nftworlds.wallet.contracts.wrappers.common.ERC20;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wallet.objects.TransactionObjects;
import com.nftworlds.wallet.objects.Wallet;
import org.bukkit.entity.Player;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class WalletAPI {

    /**
     * This function returns an NFTPlayer object that represents the player with the given UUID.
     *
     * @param uuid The UUID of the player you want to get the NFTPlayer object of.
     * @return A NFTPlayer object.
     */
    public NFTPlayer getNFTPlayer(UUID uuid) {
        return NFTPlayer.getByUUID(uuid);
    }

    /**
     * Get the NFTPlayer object for the given player.
     *
     * @param player The player you want to get the NFTPlayer of.
     * @return The NFTPlayer object that is associated with the player.
     */
    public NFTPlayer getNFTPlayer(Player player) {
        return NFTPlayer.getByUUID(player.getUniqueId());
    }

    /**
     * It returns a list of wallets for a player
     *
     * @param player The player you want to get the wallets of.
     * @return A list of wallets.
     */
    public List<Wallet> getWallets(Player player) {
        return getWallets(player.getUniqueId());
    }

    /**
     * Get the wallets of a player
     *
     * @param uuid The UUID of the player you want to get the wallets of.
     * @return A list of wallets
     */
    public List<Wallet> getWallets(UUID uuid) {
        NFTPlayer player = NFTPlayer.getByUUID(uuid);
        if (player != null) {
            return player.getWallets();
        }
        return null;
    }

    /**
     * This function returns the primary wallet of a player.
     *
     * @param player The player you want to get the wallet of.
     * @return The primary wallet of the player.
     */
    public Wallet getPrimaryWallet(Player player) {
        return getPrimaryWallet(player.getUniqueId());
    }

    /**
     * Get the primary wallet of the player with the given UUID.
     *
     * @param uuid The UUID of the player you want to get the wallet of.
     * @return The primary wallet of the player.
     */
    public Wallet getPrimaryWallet(UUID uuid) {
        NFTPlayer player = NFTPlayer.getByUUID(uuid);
        if (player != null) {
            return player.getPrimaryWallet();
        }
        return null;
    }

    /**
     * Request a WRLD from the player.
     * player -> server transaction
     *
     * @param uuid The UUID of the player to request WRLD from.
     * @param amount The amount of WRLD to request.
     * @param network The network to send the request to.
     * @param reason The reason for the request. This will be displayed to the user.
     * @param canDuplicate If true, the player will be able to duplicate the request.
     * @param payload This is the payload that will be sent to the player.
     *                It can be anything, but it's recommended to use a String.
     */
    public <T> void requestWRLD(UUID uuid, double amount, Network network, String reason, boolean canDuplicate, T payload) throws IOException, InterruptedException {
        NFTPlayer player = NFTPlayer.getByUUID(uuid);
        if (player != null) {
            player.requestWRLD(amount, network, reason, canDuplicate, payload);
        }
    }

    /**
     * Send a player WRLD.
     * server -> player transaction
     *
     * @param uuid The UUID of the player you want to send WRLD to.
     * @param amount The amount of WRLD to send.
     * @param network The network you want to send the WRLD to.
     * @param reason The reason for the transaction.
     */
    public void sendWRLD(UUID uuid, double amount, Network network, String reason) {
        NFTPlayer player = NFTPlayer.getByUUID(uuid);
        if (player != null) {
            player.sendWRLD(amount, network, reason);
        }
    }

    /**
     * Send a player WRLD.
     * server -> player transaction
     *
     * @param player The player to send the WRLD to.
     * @param amount The amount of WRLD to send.
     * @param network The network you want to send the WRLD to.
     * @param reason The reason for the transaction.
     */
    public void sendWRLD(Player player, double amount, Network network, String reason) {
        NFTPlayer p = NFTPlayer.getByUUID(player.getUniqueId());
        if (p != null) {
            p.sendWRLD(amount, network, reason);
        }
    }

    /**
     * Create a payment from one player to another.
     * player -> player transaction
     *
     * @param from The player who is sending the payment
     * @param to The player who is receiving the payment
     * @param amount The amount of money to send
     * @param network The network you want to use.
     * @param reason The reason for the payment.
     */
    public void createPlayerPayment(Player from, Player to, double amount, Network network, String reason) {
        NFTPlayer nftPlayerFrom = NFTPlayer.getByUUID(from.getUniqueId());
        NFTPlayer nftPlayerTo = NFTPlayer.getByUUID(to.getUniqueId());
        if (nftPlayerFrom != null && nftPlayerTo != null) {
            nftPlayerFrom.createPlayerPayment(nftPlayerTo, amount, network, reason);
        }
    }

    /**
     * It takes a contract address and a network, and adds the contract to the list of custom tokens
     *
     * @param contractAddress The address of the contract you want to register.
     * @param network The network you want to register the token on.
     */
    public void registerERC20(String contractAddress, Network network) {
        if (network.equals(Network.POLYGON)) {
            ERC20 newToken = ERC20.load(
                    contractAddress,
                    NFTWorlds.getInstance().getPolygonRPC().getPolygonWeb3j(),
                    TransactionObjects.polygonTransactionManager,
                    TransactionObjects.fastGasProviderPolygon
            );
            Wallet.getCustomPolygonTokenWrappers().put(contractAddress, newToken);
        } else if (network.equals(Network.ETHEREUM)) {
            ERC20 newToken = ERC20.load(
                    contractAddress,
                    NFTWorlds.getInstance().getEthereumRPC().getEthereumWeb3j(),
                    TransactionObjects.ethereumTransactionManager,
                    new DefaultGasProvider()
            );
            Wallet.getCustomPolygonTokenWrappers().put(contractAddress, newToken);
        }
    }

}
