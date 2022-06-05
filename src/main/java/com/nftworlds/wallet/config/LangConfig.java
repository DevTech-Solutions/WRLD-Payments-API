package com.nftworlds.wallet.config;

import com.nftworlds.wallet.NFTWorlds;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class LangConfig {

    private File langFile;
    private FileConfiguration lang;

    private String noLinkedWallet;
    private String incomingRequest;
    private String incomingPending;
    private String payHere;
    private String paid;
    private String setPrimaryWallet;
    private String setSecondaryWallet;
    private String removeSecondaryWallet;
    private String scanQRCode;
    private String playerNoLinkedWallet;

    /**
     * It loads the lang.yml file, and then sets the variables to the values in the file
     */
    public void registerConfig() {
        langFile = new File(NFTWorlds.getInstance().getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            NFTWorlds.getInstance().saveResource("lang.yml", false);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        this.noLinkedWallet = lang.getString("NoLinkedWallet");
        this.incomingRequest = lang.getString("IncomingRequest");
        this.incomingPending = lang.getString("IncomingPending");
        this.payHere = lang.getString("PayHere");
        this.paid = lang.getString("Paid");
        this.setPrimaryWallet = lang.getString("SetPrimaryWallet");
        this.setSecondaryWallet = lang.getString("SetSecondaryWallet");
        this.removeSecondaryWallet = lang.getString("RemoveSecondaryWallet");
        this.scanQRCode = lang.getString("ScanQRCode");
        this.playerNoLinkedWallet = lang.getString("PlayerNoLinkedWallet");
    }

}
