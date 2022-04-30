package com.nftworlds.wallet;

import com.nftworlds.wallet.commands.WalletGUICommand;
import com.nftworlds.wallet.config.Config;
import com.nftworlds.wallet.contracts.nftworlds.Players;
import com.nftworlds.wallet.contracts.nftworlds.WRLD;
import com.nftworlds.wallet.handlers.TimeoutHandler;
import com.nftworlds.wallet.listeners.PlayerListener;
import com.nftworlds.wallet.menus.WalletGUI;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Wallet;
import com.nftworlds.wallet.rpcs.Ethereum;
import com.nftworlds.wallet.rpcs.Polygon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.value.CaseInsensitiveConcurrentMap;

import java.util.Map;

public class NFTWorlds extends JavaPlugin {
    private static NFTWorlds plugin;

    @Getter private Config nftConfig;

    //Contracts
    @Getter private Players players;
    @Getter private WRLD wrld;

    //RPCs
    @Getter private Polygon polygonRPC;
    @Getter private Ethereum ethereumRPC;

    private final Map<String, Wallet> wallets = new CaseInsensitiveConcurrentMap<>();

    public void onEnable() {
        plugin = this;

        (nftConfig = new Config()).registerConfig();

        polygonRPC = new Polygon();
        ethereumRPC = new Ethereum();

        players = new Players();
        wrld = new WRLD();

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
        plugin = null;
        getServer().getConsoleSender().sendMessage("NFTWorlds WRLD API has been disabled");
    }

    public void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), this);
    }

    public void registerCommands() {
        getCommand("wallet").setExecutor(new WalletGUICommand());
    }

    public void addWallet(Wallet wallet) {
        wallets.put(wallet.getAddress(), wallet);
    }

    public Wallet getWallet(String address) {
        return wallets.get(address);
    }

    public void removeWallet(Wallet wallet) {
        wallets.remove(wallet.getAddress(), wallet);
    }

    public static NFTWorlds getInstance() {
        return plugin;
    }
}
