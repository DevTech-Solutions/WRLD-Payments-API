package com.nftworlds.wallet.config;

import com.nftworlds.wallet.NFTWorlds;
import com.nftworlds.wallet.util.StringUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.util.Objects;
import java.util.logging.Level;

@Getter
public class Config {
    private static final String DEFAULT_PRIVATE_KEY = "0x0000000000000000000000000000000000000000000000000000000000000000";

    private String polygonHttpsRpc;
    private String ethereumHttpsRpc;
    private String serverWalletAddress;
    private String serverPrivateKey;
    private String hotwalletHttpsEndpoint;

    private String polygonPlayerContract;
    private String polygonWrldContract;
    private String ethereumWrldContract;

    private int linkTimeout; //Link timeout in minutes

    private boolean debug;
    private boolean useHotwalletForOutgoingTransactions;

    /**
     * This function reads the config file and sets the variables in the Config class
     */
    public void registerConfig() {
        final NFTWorlds wallet = NFTWorlds.getInstance();
        final FileConfiguration config = wallet.getConfig();
        config.options().copyDefaults(true);
        wallet.saveConfig();

        this.polygonHttpsRpc = config.getString("polygon_https_rpc");
        this.ethereumHttpsRpc = config.getString("ethereum_https_rpc");
        this.hotwalletHttpsEndpoint = config.getString("hotwallet_https_endpoint");
        this.serverPrivateKey = config.getString("server_wallet_private_key");
        this.linkTimeout = config.getInt("link-timeout");
        this.debug = config.getBoolean("debug");

        // This is checking if the polygonHttpsRpc and ethereumHttpsRpc are empty.
        // If they are empty, it will log a warning and exit the program.
        if (StringUtil.isNullOrEmpty(this.polygonHttpsRpc) || StringUtil.isNullOrEmpty(this.ethereumHttpsRpc)) {
            NFTWorlds.getInstance().getLogger().log(Level.SEVERE, "polygon_https_rpc and ethereum_https_rpc are not set! Please set an HTTPS endpoint for an Ethereum and Polygon node. We recommend using Alchemy or Infura which will allow you to get started in five minutes!");
            NFTWorlds.getInstance().getLogger().log(Level.SEVERE, "Shutting down server. You must configure polygon_https_rpc and ethereum_https_rpc to use WRLD-Payments-API. Please see docs at https://dev.nftworlds.com/payments/wrld-payments-api#configuring-ethereum-and-polygon-rpc-endpoints");

            Bukkit.shutdown();

            // Why kill the process!?
            // Just shutdown the server..
            // System.exit(-1);
        }

        // This is checking if the server_wallet_private_key is set in the config.yml.
        // If it is set, then it will set the serverPrivateKey variable to the private key.
        // If it is not set, then it will set the serverPrivateKey to a default value.
        if (StringUtil.nonNullAndNotEmpty(this.serverPrivateKey)) {
            NFTWorlds.getInstance().getLogger().warning("A private key has been set in the plugin config! Only install plugins you trust.");
            this.useHotwalletForOutgoingTransactions = false;
        } else {
            this.serverPrivateKey = DEFAULT_PRIVATE_KEY;
        }

        // This is checking if the hotwalletHttpsEndpoint field is set in the config.yml.
        // If it is set, then it will set the useHotwalletForOutgoingTransactions variable to true.
        // If it is not set, then it will set the useHotwalletForOutgoingTransactions to false.
        this.useHotwalletForOutgoingTransactions = StringUtil.nonNullAndNotEmpty(this.hotwalletHttpsEndpoint);

        // Validate server_wallet_address
        final String address = config.getString("server_wallet_address");
        if (this.validateAddress(address, "Server Wallet Address")) {
            this.serverWalletAddress = address;
        }

        // Validate polygon_player_contract
        final String polygonPlayerContract = config.getString("contracts.polygon_player_contract");
        if (this.validateAddress(polygonPlayerContract, "Polygon Player Contract")) {
            this.polygonPlayerContract = polygonPlayerContract;
        }

        // Validate polygon_wrld_contract
        final String polygonWrldContract = config.getString("contracts.polygon_wrld_contract");
        if (this.validateAddress(polygonWrldContract, "Polygon WRLD Contract")) {
            this.polygonWrldContract = polygonWrldContract;
        }

        // Validate ethereum_wrld_contract
        final String ethereumWrldContract = config.getString("contracts.ethereum_wrld_contract");
        if (this.validateAddress(ethereumWrldContract, "Ethereum WRLD Contract")) {
            this.ethereumWrldContract = ethereumWrldContract;
        }
    }

    /**
     * If the address is not a valid address or the checksummed address is not the same as the address,
     * then log a warning and disable the plugin
     *
     * @param address The address of the contract.
     * @param name The name of the world.
     * @return A boolean value.
     */
    private boolean validateAddress(String address, String name) {
        if (!WalletUtils.isValidAddress(address) || !Keys.toChecksumAddress(address).equalsIgnoreCase(address)) {
            NFTWorlds.getInstance().getLogger().log(Level.WARNING, name + " is an invalid format. Check config.yml.");
            Bukkit.getServer().getPluginManager().disablePlugin(NFTWorlds.getInstance());
            return false;
        }

        return true;
    }

}
