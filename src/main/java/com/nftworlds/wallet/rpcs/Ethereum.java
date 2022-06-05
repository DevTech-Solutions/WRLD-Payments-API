package com.nftworlds.wallet.rpcs;

import com.nftworlds.wallet.NFTWorlds;
import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

public class Ethereum {
    private final Web3j ethereumWeb3j;
    private final DefaultGasProvider gasProvider;

    public Ethereum() {
        final String ethereumHttpsRpc = NFTWorlds.getInstance().getNftConfig().getEthereumHttpsRpc();
        final HttpService httpService = new HttpService(ethereumHttpsRpc);
        this.ethereumWeb3j = Web3j.build(httpService);
        this.gasProvider = new DefaultGasProvider();
    }

    /**
     * This function returns the web3j object that is used to connect to the Ethereum network
     *
     * @return The Web3j object.
     */
    public @NotNull Web3j getEthereumWeb3j() {
        return ethereumWeb3j;
    }

    /**
     * This function returns the gas provider.
     *
     * @return The gas provider.
     */
    public @NotNull DefaultGasProvider getGasProvider() {
        return gasProvider;
    }
}
