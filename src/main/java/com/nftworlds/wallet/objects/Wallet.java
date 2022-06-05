package com.nftworlds.wallet.objects;

import com.nftworlds.wallet.NFTWorlds;
import com.nftworlds.wallet.contracts.wrappers.common.ERC20;
import com.nftworlds.wallet.contracts.wrappers.common.ERC721;
import com.nftworlds.wallet.contracts.wrappers.polygon.PolygonWRLDToken;
import com.nftworlds.wallet.event.AsyncPlayerPaidFromServerWalletEvent;
import com.nftworlds.wallet.objects.payments.PaymentRequest;
import com.nftworlds.wallet.objects.payments.PeerToPeerPayment;
import com.nftworlds.wallet.qrmaps.LinkUtils;
import com.nftworlds.wallet.qrmaps.QRMapManager;
import com.nftworlds.wallet.util.ColorUtil;
import com.nftworlds.wallet.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Wallet {

    private final NFTPlayer owner;
    private final String address;

    private double polygonWRLDBalance;
    private double ethereumWRLDBalance;

    private static Map<String, ERC20> customPolygonTokenWrappers = new HashMap<>();
    private static Map<String, Double> customPolygonBalances = new HashMap<>();
    private static Map<String, ERC20> customEthereumTokenWrappers = new HashMap<>();
    private static Map<String, Double> customEthereumBalances = new HashMap<>();

    public Wallet(@NotNull UUID uuid, @NotNull String address) {
        this.owner = NFTPlayer.getByUUID(uuid);
        this.address = address;

        //Get balance initially
        try {
            final BigInteger bigIntegerPoly = NFTWorlds.getInstance().getWrld().getPolygonBalance(address);
            final BigInteger bigIntegerEther = NFTWorlds.getInstance().getWrld().getEthereumBalance(address);
            this.polygonWRLDBalance = Convert.fromWei(bigIntegerPoly.toString(), Convert.Unit.ETHER).doubleValue();
            this.ethereumWRLDBalance = Convert.fromWei(bigIntegerEther.toString(), Convert.Unit.ETHER).doubleValue();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        NFTWorlds.getInstance().addWallet(this);
    }

    public Wallet(@NotNull NFTPlayer owner, @NotNull String address) {
        this.owner = owner;
        this.address = address;

        try {
            BigInteger bigIntegerPoly = NFTWorlds.getInstance().getWrld().getPolygonBalance(address);
            BigInteger bigIntegerEther = NFTWorlds.getInstance().getWrld().getEthereumBalance(address);
            this.polygonWRLDBalance = Convert.fromWei(bigIntegerPoly.toString(), Convert.Unit.ETHER).doubleValue();
            this.ethereumWRLDBalance = Convert.fromWei(bigIntegerEther.toString(), Convert.Unit.ETHER).doubleValue();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        NFTWorlds.getInstance().addWallet(this);
    }

    /**
     * If the network is Polygon, return the Polygon balance, otherwise return the Ethereum balance.
     *
     * @param network The network you want to get the balance for.
     * @return The balance of the wallet on the specified network.
     */
    public double getWRLDBalance(Network network) {
        if (network == Network.POLYGON) {
            return polygonWRLDBalance;
        }
        return ethereumWRLDBalance;
    }

    /**
     * Refresh the wallet's balance for an arbitrary ERC20 token defined at runtime.
     * This is a blocking call, do not run in main thread.
     */
    public void refreshERC20Balance(Network network, String tokenContract) throws Exception {
        if (network == Network.POLYGON) {
            ERC20 customToken = Wallet.getCustomPolygonTokenWrappers().get(tokenContract);
            if (customToken == null) {
                final Web3j polygonWeb3j = NFTWorlds.getInstance().getPolygonRPC().getPolygonWeb3j();
                final String serverPrivateKey = NFTWorlds.getInstance().getNftConfig().getServerPrivateKey();
                customToken = ERC20.load(tokenContract, polygonWeb3j, Credentials.create(serverPrivateKey), new DefaultGasProvider());
                Wallet.getCustomPolygonTokenWrappers().put(tokenContract, customToken);
            }

            final BigInteger bigInteger = customToken.balanceOf(address).send();
            customPolygonBalances.put(tokenContract, Convert.fromWei(bigInteger.toString(), Convert.Unit.ETHER).doubleValue());
            return;
        }

        ERC20 customToken = Wallet.getCustomEthereumTokenWrappers().get(tokenContract);
        if (Objects.isNull(customToken)) {
            final Web3j ethereumWeb3j = NFTWorlds.getInstance().getEthereumRPC().getEthereumWeb3j();
            final String serverPrivateKey = NFTWorlds.getInstance().getNftConfig().getServerPrivateKey();
            customToken = ERC20.load(tokenContract, ethereumWeb3j, Credentials.create(serverPrivateKey), new DefaultGasProvider());
            Wallet.getCustomPolygonTokenWrappers().put(tokenContract, customToken);
        }

        final BigInteger bigInteger = customToken.balanceOf(address).send();
        customEthereumBalances.put(tokenContract, Convert.fromWei(bigInteger.toString(), Convert.Unit.ETHER).doubleValue());
    }

    /**
     * This function takes in a contract address and returns a JSON object containing all the NFTs owned by the address.<br>
     *
     * Alternative API for NFT fetching that seems to provide better data than Alchemy.
     * Returns NFTs on both Polygon and Ethereum chains.
     *
     * @param contractAddress The address of the contract that owns the NFTs.
     * @return A JSON object containing the NFTs owned by the address.
     */
    public @NotNull JSONObject getOwnedNFTsByContactWithSimpleHash(@NotNull String contractAddress) throws URISyntaxException, IOException, InterruptedException {
        final String url = """
                https://api.simplehash.com/api/v0/nfts/owners?\
                chains=polygon,\
                ethereum&wallet_addresses=%s\
                &contract_addresses=%s
                """.formatted(address, contractAddress);

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url)).header("X-API-KEY", "worldql_sk_ssga6syqc1eo5eyn")
                .build();
        return new JSONObject(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
    }

    /**
     * It gets all the NFTs owned by the player.
     *
     * Get a list of all the account's owned NFTs. Does not return metadata.
     * This is a blocking call, do not run in main thread.
     *
     * @param network The network you want to get the NFTs from.
     * @return A JSONObject containing the NFTs owned by the address.
     */
    public @NotNull JSONObject getOwnedNFTs(@NotNull Network network) throws IOException, InterruptedException {
        final String baseURL = switch (network) {
            case ETHEREUM -> NFTWorlds.getInstance().getNftConfig().getEthereumHttpsRpc();
            case POLYGON -> NFTWorlds.getInstance().getNftConfig().getPolygonHttpsRpc();
        };
        final String url = "%s/getNFTs?owner=%s&withMetadata=false".formatted(baseURL, address);
        return new JSONObject(HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString())
                .body());
    }

    /**
     * Get a list of all the account's owned NFTs. Returns metadata.
     */
    public JSONObject getOwnedNFTsFromContract(@NotNull Network network, @NotNull String contractAddress) throws IOException, InterruptedException {
        final String baseURL = switch (network) {
            case ETHEREUM -> NFTWorlds.getInstance().getNftConfig().getEthereumHttpsRpc();
            case POLYGON -> NFTWorlds.getInstance().getNftConfig().getPolygonHttpsRpc();
        };
        final String url = "%s/getNFTs?owner=%s&contractAddresses[]=%s".formatted(baseURL, address, contractAddress);
        return new JSONObject(HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString())
                .body());
    }

    /**
     * It checks if the player owns at least one NFT from the given contract address
     *
     * @param network The network the contract is on.
     * @param contractAddress The address of the contract you want to check if the player owns an NFT from.
     * @return A boolean value.
     */
    public boolean doesPlayerOwnNFTInCollection(@NotNull Network network, @NotNull String contractAddress) {
        final ERC721 erc721 = switch (network) {
            case ETHEREUM -> {
                final Web3j ethereumWeb3j = NFTWorlds.getInstance().getEthereumRPC().getEthereumWeb3j();
                final String serverPrivateKey = NFTWorlds.getInstance().getNftConfig().getServerPrivateKey();
                yield ERC721.load(contractAddress, ethereumWeb3j, Credentials.create(serverPrivateKey), new DefaultGasProvider());
            }

            case POLYGON -> {
                final Web3j polygonWeb3j = NFTWorlds.getInstance().getPolygonRPC().getPolygonWeb3j();
                final String serverPrivateKey = NFTWorlds.getInstance().getNftConfig().getServerPrivateKey();
                yield ERC721.load(contractAddress, polygonWeb3j, Credentials.create(serverPrivateKey), new DefaultGasProvider());
            }
        };

        try {
            final BigInteger balance = erc721.balanceOf(address).send();
            return balance.compareTo(BigInteger.ZERO) > 0; //return true if address owns at least 1 NFT from this contract
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
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
        final NFTWorlds nftWorlds = NFTWorlds.getInstance();

        final UUID uuid = owner.getUuid();
        final Player player = Bukkit.getPlayer(uuid);
        if (Objects.isNull(player))
            return;

        //NOTE: This generates a random Uint256 to use as a reference. Don't know if we want to change this or not.
        final Uint256 refID = new Uint256(new BigInteger(256, new Random()));

        final long timeout = Instant.now().plus(nftWorlds.getNftConfig().getLinkTimeout(), ChronoUnit.SECONDS).toEpochMilli();
        final PaymentRequest<T> request = new PaymentRequest<>(uuid, amount, refID, network, reason, timeout, canDuplicate, payload);

        final String serverWalletAddress = nftWorlds.getNftConfig().getServerWalletAddress();
        final String paymentLink = """
                    https://nftworlds.com/pay/?to=%s\
                    &amount=%s\
                    &ref=%s\
                    &expires=%s\
                    &duplicate=%s
                    """.formatted(serverWalletAddress, amount, refID.getValue().toString(), (int) (timeout / 1000), canDuplicate);


        final MapView view = Bukkit.createMap(player.getWorld());
        view.getRenderers().clear();

        final QRMapManager renderer = new QRMapManager();
        final String incomingRequest = NFTWorlds.getInstance().getLangConfig().getIncomingRequest();
        player.sendMessage(ColorUtil.rgb(incomingRequest.formatted(reason)));

        if (Bukkit.getServer().getPluginManager().getPlugin("Geyser-Spigot") != null && org.geysermc.connector.GeyserConnector.getInstance().getPlayerByUuid(player.getUniqueId()) != null) {
            final String shortLink = LinkUtils.shortenURL(paymentLink);
            renderer.load(shortLink);
            // TODO: Better error handling
            view.addRenderer(renderer);
            final ItemStack map = new ItemStack(Material.FILLED_MAP);
            final MapMeta meta = (MapMeta) map.getItemMeta();

            meta.setMapView(view);
            map.setItemMeta(meta);

            QRMapManager.playerPreviousItem.put(player.getUniqueId(), player.getInventory().getItem(0));
            player.getInventory().setItem(0, map);
            player.getInventory().setHeldItemSlot(0);

            final String scanQRCode = NFTWorlds.getInstance().getLangConfig().getScanQRCode();
            player.sendMessage(ColorUtil.rgb(scanQRCode));
            return;
        }

        final String payHere = NFTWorlds.getInstance().getLangConfig().getPayHere();
        player.sendMessage(ColorUtil.rgb(payHere.formatted(paymentLink)));
    }

    /**
     * It sends a transaction to the player's wallet
     *
     * @param amount The amount of WRLD to send.
     * @param network The network to send the payment to. Currently only Polygon is supported.
     * @param reason The reason for the payment. This will be displayed to the player.
     */
    public void payWRLD(double amount, @NotNull Network network, @NotNull String reason) {
        if (!owner.isLinked()) {
            NFTWorlds.getInstance().getLogger().warning("Skipped outgoing transaction because wallet was not linked!");
            return;
        }

        if (!network.equals(Network.POLYGON)) {
            NFTWorlds.getInstance().getLogger().warning("Attempted to call Wallet.payWRLD with unsupported network. Only Polygon is supported in this plugin at the moment.");
            return;
        }

        final UUID playerUniqueId = owner.getUuid();
        final String incomingRequest = NFTWorlds.getInstance().getLangConfig().getIncomingRequest();
        PlayerUtil.sendActionBar(playerUniqueId, ColorUtil.rgb(incomingRequest.formatted(amount)));

        final BigDecimal sending = Convert.toWei(BigDecimal.valueOf(amount), Convert.Unit.ETHER);

        if (!NFTWorlds.getInstance().getNftConfig().isUseHotwalletForOutgoingTransactions()) {
            try {
                NFTWorlds.getInstance().getLogger().info("Sending outgoing transaction using PK to " + PlayerUtil.getName(playerUniqueId) + " for " + amount);

                final PolygonWRLDToken polygonWRLDTokenContract = NFTWorlds.getInstance().getWrld().getPolygonWRLDTokenContract();

                polygonWRLDTokenContract.transfer(this.getAddress(), sending.toBigInteger()).sendAsync().thenAccept((receipt) -> {
                    final String receiptLink = "https://polygonscan.com/tx/%s".formatted(receipt.getTransactionHash());
                    final Player player = Bukkit.getPlayer(playerUniqueId);
                    final AsyncPlayerPaidFromServerWalletEvent walletEvent = new AsyncPlayerPaidFromServerWalletEvent(player, amount, network, reason, receiptLink);
                    walletEvent.callEvent();

                    if (walletEvent.isDefaultReceiveMessage()) {
                        final String paid = NFTWorlds.getInstance().getLangConfig().getPaid();
                        PlayerUtil.sendMessage(playerUniqueId, ColorUtil.rgb(paid.formatted(reason, receiptLink)));
                    }
                }).exceptionally(error -> {
                    NFTWorlds.getInstance().getLogger().warning("Caught error in transfer function exceptionally: " + error);
                    return null;
                });
            } catch (Exception exception) {
                NFTWorlds.getInstance().getLogger().warning("caught error in payWrld:");
                throw new RuntimeException(exception);
            }
            return;
        }

        // TODO: Add support for other outgoing currencies through Hotwallet.
        final JSONObject json = new JSONObject()
                .put("network", "Polygon")
                .put("token", "POLYGON_WRLD")
                .put("recipient_address", this.getAddress())
                .put("amount", sending.toBigInteger());

        final String requestBody = json.toString();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(NFTWorlds.getInstance().getNftConfig().getHotwalletHttpsEndpoint() + "/send_tokens"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            final JSONObject response = new JSONObject(HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body());
            final String receiptLink = "https://app.economykit.com/hotwallet/transaction/" + response.getInt("outgoing_tx_id");
            Bukkit.getScheduler().runTaskAsynchronously(NFTWorlds.getInstance(), () -> {
                final Player player = Bukkit.getPlayer(playerUniqueId);
                final AsyncPlayerPaidFromServerWalletEvent walletEvent = new AsyncPlayerPaidFromServerWalletEvent(player, amount, network, reason, receiptLink);
                walletEvent.callEvent();

                if (walletEvent.isDefaultReceiveMessage()) {
                    final String paid = NFTWorlds.getInstance().getLangConfig().getPaid();
                    PlayerUtil.sendMessage(playerUniqueId, ColorUtil.rgb(paid.formatted(reason, receiptLink)));
                }
            });
        } catch (IOException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }
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
        final NFTWorlds nftWorlds = NFTWorlds.getInstance();
        final Player player = Bukkit.getPlayer(owner.getUuid());
        if (Objects.isNull(player))
            return;

        final String playerNoLinkedWallet = NFTWorlds.getInstance().getLangConfig().getPlayerNoLinkedWallet();
        if (!to.isLinked()) {
            player.sendMessage(ColorUtil.rgb(playerNoLinkedWallet));
            return;
        }

        final Uint256 refID = new Uint256(new BigInteger(256, new Random()));
        final long timeout = Instant.now().plus(nftWorlds.getNftConfig().getLinkTimeout(), ChronoUnit.SECONDS).toEpochMilli();
        new PeerToPeerPayment(to, owner, amount, refID, network, reason, timeout);

        final String paymentLink = """
                https://nftworlds.com/pay/?to=%s\
                &amount=%s\
                &ref=%s\
                &expires=%s
                """.formatted(to.getPrimaryWallet().getAddress(), amount, refID.getValue().toString(), (int) (timeout / 1000));

        final String payHere = NFTWorlds.getInstance().getLangConfig().getPayHere();
        player.sendMessage(ColorUtil.rgb(payHere.formatted(paymentLink)));
    }

    /**
     * Returns the owner of this Wallet
     *
     * @return The owner of the Wallet.
     */
    public @NotNull NFTPlayer getOwner() {
        return owner;
    }

    /**
     * This function returns the address of the Wallet
     *
     * @return The address of the Wallet.
     */
    public @NotNull String getAddress() {
        return address;
    }

    /**
     * This function returns the balance of the polygon in the WRLD currency
     *
     * @return The value of the variable polygonWRLDBalance.
     */
    public double getPolygonWRLDBalance() {
        return polygonWRLDBalance;
    }

    /**
     * This function sets the balance of the polygon WRLD account
     *
     * @param polygonWRLDBalance The amount of WRLD you want to buy.
     */
    public void setPolygonWRLDBalance(double polygonWRLDBalance) {
        this.polygonWRLDBalance = polygonWRLDBalance;
    }

    /**
     * This function returns the value of the ethereum WRLD Balance
     *
     * @return The ethereumWRLDBalance variable is being returned.
     */
    public double getEthereumWRLDBalance() {
        return ethereumWRLDBalance;
    }

    /**
     * This function sets the balance of the ethereum WRLD account
     *
     * @param ethereumWRLDBalance The amount of WRLD you want to buy.
     */
    public void setEthereumWRLDBalance(double ethereumWRLDBalance) {
        this.ethereumWRLDBalance = ethereumWRLDBalance;
    }

    /**
     * It returns a map of the balances of all the custom polygons
     *
     * @return A map of the balances of the custom polygons.
     */
    public static @NotNull Map<String, Double> getCustomPolygonBalances() {
        return customPolygonBalances;
    }

    /**
     * It returns a map of all the custom Ethereum balances
     *
     * @return A map of the custom ethereum balances.
     */
    public static @NotNull Map<String, Double> getCustomEthereumBalances() {
        return customEthereumBalances;
    }

    /**
     * It returns a map of all the custom ERC20 tokens that have been added to the wallet
     *
     * @return A map of custom Ethereum token wrappers.
     */
    public static @NotNull Map<String, ERC20> getCustomEthereumTokenWrappers() {
        return customEthereumTokenWrappers;
    }

    /**
     * It returns a map of all the custom tokens that have been added to the Polygon SDK
     *
     * @return A map of custom token wrappers.
     */
    public static @NotNull Map<String, ERC20> getCustomPolygonTokenWrappers() {
        return customPolygonTokenWrappers;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final Wallet wallet = (Wallet) object;
        if (!Objects.equals(owner, wallet.owner)) return false;
        return Objects.equals(address, wallet.address);
    }

    @Override
    public int hashCode() {
        int result = owner != null ? owner.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

}
