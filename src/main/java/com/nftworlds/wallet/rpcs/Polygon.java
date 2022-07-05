package com.nftworlds.wallet.rpcs;

import com.nftworlds.wallet.NFTWorlds;
import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

public class Polygon {
    private final Web3j polygonWeb3j;
    private final DefaultGasProvider gasProvider;

    /**
     * [13:36:27 ERROR]: [org.web3j.protocol.core.filters.Filter] Error sending request
     * org.web3j.protocol.exceptions.ClientConnectionException: Invalid response received: 429;
     *          {
     *              "jsonrpc":"2.0",
     *              "error":{
     *                  "code":429,
     *                  "message":"Your app has exceeded its compute units per second capacity.
     *                             If you have retries enabled, you can safely ignore this message.
     *                             If not, check out https://docs.alchemyapi.io/guides/rate-limits"
     *              },
     *              "id":84
     *         }
     *
     *         at org.web3j.protocol.http.HttpService.performIO(HttpService.java:173) ~[wrld-payments-api.jar:?]
     *         at org.web3j.protocol.Service.send(Service.java:48) ~[wrld-payments-api.jar:?]
     *         at org.web3j.protocol.core.Request.send(Request.java:87) ~[wrld-payments-api.jar:?]
     *         at org.web3j.protocol.core.filters.Filter.pollFilter(Filter.java:129) ~[wrld-payments-api.jar:?]
     *         at org.web3j.protocol.core.filters.Filter.lambda$run$0(Filter.java:92) ~[wrld-payments-api.jar:?]
     *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539) ~[?:?]
     *         at java.util.concurrent.FutureTask.runAndReset(FutureTask.java:305) ~[?:?]
     *         at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:305) ~[?:?]
     *         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136) ~[?:?]
     *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635) ~[?:?]
     *         at java.lang.Thread.run(Thread.java:833) ~[?:?]
     */

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
