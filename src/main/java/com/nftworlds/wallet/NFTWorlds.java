package com.nftworlds.wallet;

import com.nftworlds.wallet.commands.WalletGUICommand;
import com.nftworlds.wallet.config.Config;
import com.nftworlds.wallet.config.LangConfig;
import com.nftworlds.wallet.contracts.nftworlds.Players;
import com.nftworlds.wallet.contracts.nftworlds.WRLD;
import com.nftworlds.wallet.handlers.TimeoutHandler;
import com.nftworlds.wallet.listeners.PlayerListener;
import com.nftworlds.wallet.menus.WalletGUI;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Wallet;
import com.nftworlds.wallet.rpcs.Ethereum;
import com.nftworlds.wallet.rpcs.Polygon;
import io.reactivex.disposables.Disposable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.value.CaseInsensitiveConcurrentMap;

import java.util.Map;
import java.util.Objects;

public class NFTWorlds extends JavaPlugin {
    private static NFTWorlds PLUGIN;

    @Getter private Config nftConfig;

    @Getter private LangConfig langConfig;

    //Contracts
    @Getter private Players players;
    @Getter private WRLD wrld;

    //RPCs
    @Getter private Polygon polygonRPC;
    @Getter private Ethereum ethereumRPC;

    private final Map<String, Wallet> wallets = new CaseInsensitiveConcurrentMap<>();

    public void onEnable() {
        NFTWorlds.PLUGIN = this;

        (this.nftConfig = new Config()).registerConfig();
        (this.langConfig = new LangConfig()).registerConfig();

        this.polygonRPC = new Polygon();
        this.ethereumRPC = new Ethereum();

        this.players = new Players();
        this.wrld = new WRLD();

        WalletGUI.setup();

        for (Player p : Bukkit.getOnlinePlayers()) {
            new NFTPlayer(p.getUniqueId());
        }

        new TimeoutHandler().handleTimeouts();

        registerEvents();
        registerCommands();

        getServer().getConsoleSender().sendMessage("NFTWorlds WRLD API has been enabled");
    }

    public void onDisable() {
        NFTWorlds.PLUGIN = null;

        getServer().getConsoleSender().sendMessage("NFTWorlds WRLD API has been disabled");

        if (Objects.nonNull(this.polygonRPC))
            this.polygonRPC.shutdown();

        if (Objects.nonNull(this.ethereumRPC))
            this.ethereumRPC.shutdown();

        if (Objects.nonNull(this.wrld)) {
            final Disposable flowableSubscription = this.wrld.getFlowableSubscription();
            flowableSubscription.dispose();
        }
        if (Objects.nonNull(this.players)) {
            final Disposable flowableSubscription = this.players.getFlowableSubscription();
            flowableSubscription.dispose();
        }
    }

    public void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), this);
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("wallet")).setExecutor(new WalletGUICommand());
    }

    /**
     * This function adds a wallet to the wallets map.
     *
     * @param wallet The wallet to add to the wallet list.
     */
    public void addWallet(Wallet wallet) {
        wallets.put(wallet.getAddress(), wallet);
    }

    /**
     * Given an address, return the wallet associated with that address.
     *
     * @param address The address of the wallet you want to get.
     * @return The wallet object that is associated with the address.
     */
    public Wallet getWallet(String address) {
        return wallets.get(address);
    }

    /**
     * Remove the wallet from the map of wallets.
     *
     * @param wallet The wallet to remove.
     */
    public void removeWallet(Wallet wallet) {
        wallets.remove(wallet.getAddress(), wallet);
    }

    public static NFTWorlds getInstance() {
        return NFTWorlds.PLUGIN;
    }
}
