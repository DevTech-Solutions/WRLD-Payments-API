package com.nftworlds.wallet.rpcs;

import com.nftworlds.wallet.NFTWorlds;
import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

public class Polygon {
    private final Web3j polygonWeb3j;
    private final DefaultGasProvider gasProvider;

    public Polygon() {
        final String polygonHttpsRpc = NFTWorlds.getInstance().getNftConfig().getPolygonHttpsRpc();
        final HttpService httpService = new HttpService(polygonHttpsRpc);
        this.polygonWeb3j = Web3j.build(httpService);
        this.gasProvider = new DefaultGasProvider();
    }

    /**
     * This function returns the web3j object that is used to connect to the Polygon network
     *
     * @return The Web3j object.
     */
    public @NotNull Web3j getPolygonWeb3j() {
        return polygonWeb3j;
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
