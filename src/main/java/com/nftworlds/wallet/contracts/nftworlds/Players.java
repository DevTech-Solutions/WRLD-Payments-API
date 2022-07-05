package com.nftworlds.wallet.contracts.nftworlds;

import com.nftworlds.wallet.NFTWorlds;
import com.nftworlds.wallet.contracts.wrappers.polygon.PolygonPlayers;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Wallet;
import com.nftworlds.wallet.rpcs.Polygon;
import com.nftworlds.wallet.util.ColorUtil;
import io.reactivex.disposables.Disposable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Pattern;

@SuppressWarnings("DuplicatedCode")
public class Players {
    public static final String PLAYER_PRIMARY_WALLET_SET = Hash.sha3String("PlayerPrimaryWalletSet(string,string,address)");
    public static final String PLAYER_SECONDARY_WALLET_SET = Hash.sha3String("PlayerSecondaryWalletSet(string,string,address)");
    public static final String PLAYER_SECONDARY_WALLET_REMOVED = Hash.sha3String("PlayerSecondaryWalletRemoved(string,string,address)");

    private static final Pattern UUID_FROM_LOG = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");

    private final PolygonPlayers polygonPlayersContract;
    private final boolean debug;

    private Disposable flowableSubscription;

    public Players() {
        NFTWorlds nftWorlds = NFTWorlds.getInstance();
        Polygon polygonRPC = nftWorlds.getPolygonRPC();
        Credentials credentials = null;
        debug = nftWorlds.getNftConfig().isDebug();

        try {
            // We're only reading so this can be anything
            credentials = Credentials.create("0x0000000000000000000000000000000000000000000000000000000000000000");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.polygonPlayersContract = PolygonPlayers.load(
                nftWorlds.getNftConfig().getPolygonPlayerContract(),
                polygonRPC.getPolygonWeb3j(),
                credentials,
                polygonRPC.getGasProvider()
        );

        this.startPlayerWalletUpdateListener();
    }

    /**
     * > This function returns the subscription object that is used to subscribe to the flowable
     *
     * @return A Disposable object.
     */
    public @NotNull Disposable getFlowableSubscription() {
        return flowableSubscription;
    }

    /**
     * Get the primary wallet address of a player
     *
     * @param playerUUID The UUID of the player you want to get the primary wallet address of.
     * @return The primary wallet address of the player.
     */
    public String getPlayerPrimaryWallet(String playerUUID) throws Exception {
        return this.polygonPlayersContract.getPlayerPrimaryWallet(playerUUID.replace("-", "")).send();
    }

    /**
     * Get the primary wallet address for a player.
     *
     * @param playerUUID The UUID of the player you want to get the primary wallet of.
     * @return The primary wallet address of the player.
     */
    public CompletableFuture<String> getPlayerPrimaryWalletAsync(String playerUUID) throws Exception {
        return this.polygonPlayersContract.getPlayerPrimaryWallet(playerUUID.replace("-", "")).sendAsync();
    }

    /**
     * Get the secondary wallets of a player
     *
     * @param playerUUID The UUID of the player you want to get the secondary wallets for.
     * @return A list of secondary wallets for the player.
     */
    public List<String> getPlayerSecondaryWallets(String playerUUID) throws Exception {
        return this.polygonPlayersContract.getPlayerSecondaryWallets(playerUUID.replace("-", "")).send();
    }

    /**
     * > Get a list of secondary wallets for a player
     *
     * @param playerUUID The UUID of the player you want to get the secondary wallets of.
     * @return A list of secondary wallets.
     */
    public CompletableFuture<List> getPlayerSecondaryWalletsAsync(String playerUUID) throws Exception {
        return this.polygonPlayersContract.getPlayerSecondaryWallets(playerUUID.replace("-", "")).sendAsync();
    }

    public JSONObject getPlayerStateData(String playerUUID, String setterWalletAddress) throws Exception {
        String stateDataUrl = this.polygonPlayersContract.getPlayerStateData(playerUUID.replace("-", ""), setterWalletAddress, true).send();

        if (stateDataUrl.isEmpty()) {
            return null;
        }

        return new JSONObject(HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(URI.create(stateDataUrl)).build(), HttpResponse.BodyHandlers.ofString()).body());
    }

    public JSONObject getPlayerStateDataAsync(String playerUUID, String setterWalletAddress) throws Exception {
        CompletableFuture<String> stateDataUrl = this.polygonPlayersContract.getPlayerStateData(playerUUID.replace("-", ""), setterWalletAddress, true).sendAsync();

        if (stateDataUrl.get().isEmpty()) {
            return null;
        }

        return new JSONObject(HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(URI.create(stateDataUrl.get())).build(), HttpResponse.BodyHandlers.ofString()).body());
    }

    private void startPlayerWalletUpdateListener() {
        EthFilter transferFilter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                this.polygonPlayersContract.getContractAddress()
        ).addOptionalTopics(Players.PLAYER_PRIMARY_WALLET_SET, Players.PLAYER_SECONDARY_WALLET_SET, Players.PLAYER_SECONDARY_WALLET_REMOVED);

        this.flowableSubscription = NFTWorlds.getInstance().getPolygonRPC().getPolygonWeb3j().ethLogFlowable(transferFilter)
                .subscribe(log -> {
                    String eventHash = log.getTopics().get(0);

                    if (eventHash.equals(PLAYER_PRIMARY_WALLET_SET)) {
                        this.paymentListener_handlePrimaryWalletSetEvent(log);
                    } else if (eventHash.equals(PLAYER_SECONDARY_WALLET_SET)) {
                        this.paymentListener_handleSecondaryWalletSetEvent(log);
                    } else if (eventHash.equals(PLAYER_SECONDARY_WALLET_REMOVED)) {
                        this.paymentListener_handleSecondaryWalletRemovedEvent(log);
                    }
                },
                Throwable::printStackTrace
        );
    }

    @SuppressWarnings("rawtypes")
    public void paymentListener_handlePrimaryWalletSetEvent(Log log) {
        final List<String> topics = log.getTopics();
        final List<Type> data = FunctionReturnDecoder.decode(log.getData(), PolygonPlayers.PLAYERPRIMARYWALLETSET_EVENT.getNonIndexedParameters());

        final String playerUUID = (String) data.get(0).getValue();

        // It's converting the UUID from the contract to a UUID that Bukkit can use.
        final NFTPlayer nftPlayer = NFTPlayer.getByUUID(UUID.fromString(playerUUID.replaceFirst(UUID_FROM_LOG.pattern(), "$1-$2-$3-$4-$5")));
        if (Objects.isNull(nftPlayer))
            return;

        // It's getting the wallet address from the event log.
        final Address walletAddress = (Address) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>(false) {});
        nftPlayer.setPrimaryWallet(new Wallet(nftPlayer, walletAddress.getValue()));

        // Log to the console if "debug" is enabled.
        // Only log if the players are on OUR server!
        if (this.debug) {
            NFTWorlds.getInstance().getLogger().log(Level.INFO, "Primary wallet updated");
            NFTWorlds.getInstance().getLogger().log(Level.INFO, "Primary wallet of uuid %s set to %s".formatted(playerUUID, walletAddress));
        }

        // If the player is online, send a message and sound.
        final Player bukkitPlayer = Bukkit.getPlayer(nftPlayer.getUuid());
        if (Objects.nonNull(bukkitPlayer) && bukkitPlayer.isOnline()) {
            final String setPrimaryWallet = NFTWorlds.getInstance().getLangConfig().getSetPrimaryWallet();
            bukkitPlayer.sendMessage(ColorUtil.rgb(setPrimaryWallet.formatted(walletAddress.getValue())));

            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
        }

        // Clean up
        topics.clear();
        data.clear();
    }

    @SuppressWarnings("rawtypes")
    public void paymentListener_handleSecondaryWalletSetEvent(Log log) {
        final List<String> topics = log.getTopics();
        final List<Type> data = FunctionReturnDecoder.decode(log.getData(), PolygonPlayers.PLAYERSECONDARYWALLETSET_EVENT.getNonIndexedParameters());

        // It's converting the UUID from the contract to a UUID that Bukkit can use.
        final String playerUUID = (String) data.get(0).getValue();
        final NFTPlayer nftPlayer = NFTPlayer.getByUUID(UUID.fromString(playerUUID.replaceFirst(UUID_FROM_LOG.pattern(),"$1-$2-$3-$4-$5")));
        if (Objects.isNull(nftPlayer))
            return;

        // It's getting the wallet address from the event log.
        final Address walletAddress = (Address) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>(false) {});
        nftPlayer.getWallets().add(new Wallet(nftPlayer, walletAddress.getValue()));

        // Log to the console if "debug" is enabled.
        // Only log if the players are on OUR server!
        if (this.debug) {
            NFTWorlds.getInstance().getLogger().log(Level.INFO, "Secondary wallet updated (addition)");
            NFTWorlds.getInstance().getLogger().log(Level.INFO, "Added secondary wallet of %s to uuid %s".formatted(walletAddress.toString(), playerUUID));
        }

        // If the player is online, send a message and sound.
        final Player bukkitPlayer = Bukkit.getPlayer(nftPlayer.getUuid());
        if (Objects.nonNull(bukkitPlayer) && bukkitPlayer.isOnline()) {
            final String setSecondaryWallet = NFTWorlds.getInstance().getLangConfig().getSetSecondaryWallet();
            bukkitPlayer.sendMessage(ColorUtil.rgb(setSecondaryWallet.formatted(walletAddress.getValue())));

            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
        }

        // Clean up
        topics.clear();
        data.clear();
    }

    @SuppressWarnings("rawtypes")
    public void paymentListener_handleSecondaryWalletRemovedEvent(Log log) {
        final List<String> topics = log.getTopics();
        final List<Type> data = FunctionReturnDecoder.decode(log.getData(), PolygonPlayers.PLAYERSECONDARYWALLETREMOVED_EVENT.getNonIndexedParameters());

        // It's converting the UUID from the contract to a UUID that Bukkit can use.
        final String playerUUID = (String) data.get(0).getValue();
        final NFTPlayer nftPlayer = NFTPlayer.getByUUID(UUID.fromString(playerUUID.replaceFirst(UUID_FROM_LOG.pattern(), "$1-$2-$3-$4-$5")));
        if (Objects.isNull(nftPlayer))
            return;

        // It's getting the wallet address from the event log.
        final Address walletAddress = (Address) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>(false) {});
        nftPlayer.getWallets().removeIf(wallet -> wallet.getAddress().equalsIgnoreCase(walletAddress.getValue()));

        // Log to the console if "debug" is enabled.
        // Only log if the players are on OUR server!
        if (this.debug) {
            NFTWorlds.getInstance().getLogger().log(Level.INFO, "Secondary wallet updated (removal)");
            NFTWorlds.getInstance().getLogger().log(Level.INFO, "Removed secondary wallet of " +  walletAddress.toString() + " from uuid " + playerUUID);
        }

        // If the player is online, send a message and sound.
        final Player bukkitPlayer = Bukkit.getPlayer(nftPlayer.getUuid());
        if (Objects.nonNull(bukkitPlayer) && bukkitPlayer.isOnline()) {
            final String removeSecondaryWallet = NFTWorlds.getInstance().getLangConfig().getRemoveSecondaryWallet();
            bukkitPlayer.sendMessage(ColorUtil.rgb(removeSecondaryWallet.formatted(walletAddress.getValue())));

            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }

        // Clean up
        topics.clear();
        data.clear();
    }
}
