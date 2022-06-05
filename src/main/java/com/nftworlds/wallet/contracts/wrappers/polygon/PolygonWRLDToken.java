package com.nftworlds.wallet.contracts.wrappers.polygon;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contract wrapper for ERC20 NFT Worlds WRLD token on the Polygon chain.
 * Auto-generated with web3j version 4.1.1
 */

@SuppressWarnings({"DuplicatedCode", "unused"})
public class PolygonWRLDToken extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_CAP = "cap";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DECREASEALLOWANCE = "decreaseAllowance";

    public static final String FUNC_DEPOSIT = "deposit";

    public static final String FUNC_FEEBPS = "feeBps";

    public static final String FUNC_FEECAP = "feeCap";

    public static final String FUNC_FEEFIXED = "feeFixed";

    public static final String FUNC_INCREASEALLOWANCE = "increaseAllowance";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFERWITHFEE = "transferWithFee";

    public static final String FUNC_TRANSFERWITHFEEREF = "transferWithFeeRef";

    public static final String FUNC_TRANSFERWITHREF = "transferWithRef";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred",
            Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));

    public static final Event TRANSFERREF_EVENT = new Event("TransferRef",
            Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));

    @Deprecated
    protected PolygonWRLDToken(@NotNull String contractAddress,
                               @NotNull Web3j web3j,
                               @NotNull Credentials credentials,
                               @NotNull BigInteger gasPrice,
                               @NotNull BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PolygonWRLDToken(@NotNull String contractAddress,
                               @NotNull Web3j web3j,
                               @NotNull Credentials credentials,
                               @NotNull ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PolygonWRLDToken(@NotNull String contractAddress,
                               @NotNull Web3j web3j,
                               @NotNull TransactionManager transactionManager,
                               @NotNull BigInteger gasPrice,
                               @NotNull BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PolygonWRLDToken(@NotNull String contractAddress,
                               @NotNull Web3j web3j,
                               @NotNull TransactionManager transactionManager,
                               @NotNull ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    /**
     * It takes a transaction receipt as input and returns a list of ApprovalEventResponse objects
     *
     * @param transactionReceipt The transaction receipt of the transaction that triggered the event.
     * @return A list of ApprovalEventResponse objects.
     */
    public @NotNull List<ApprovalEventResponse> getApprovalEvents(@NotNull TransactionReceipt transactionReceipt) {
        final List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        final ArrayList<ApprovalEventResponse> responses = new ArrayList<>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            final ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    /**
     * This function returns a Flowable that emits ApprovalEventResponse objects,
     * which are created by extracting the event parameters from the logs of the Approval event.
     *
     * @param filter The filter object that will be used to filter the events.
     * @return A Flowable of ApprovalEventResponse objects.
     */
    public @NotNull Flowable<ApprovalEventResponse> approvalEventFlowable(@NotNull EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> {
            final EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
            final ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = log;
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            return typedResponse;
        });
    }

    /**
     * `approvalEventFlowable` is a function that returns a `Flowable` of `ApprovalEventResponse` objects
     *
     * @param startBlock The block number to start listening from.
     * @param endBlock The block number to stop getting logs from (inclusive).
     * @return A Flowable of ApprovalEventResponse objects.
     */
    public @NotNull Flowable<ApprovalEventResponse> approvalEventFlowable(@NotNull DefaultBlockParameter startBlock,
                                                                          @NotNull DefaultBlockParameter endBlock) {
        final EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    /**
     * It returns a list of OwnershipTransferredEventResponse objects,
     * which are created by extracting the indexed values from the event log
     *
     * @param transactionReceipt The transaction receipt that contains the event logs.
     * @return a list of OwnershipTransferredEventResponse objects.
     */
    public @NotNull List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(@NotNull TransactionReceipt transactionReceipt) {
        final List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        final ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            final OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    /**
     * It returns a Flowable of OwnershipTransferredEventResponse objects, which are created by extracting the event
     * parameters from the log and then mapping them to the OwnershipTransferredEventResponse object
     *
     * @param filter The filter object that will be used to filter the events.
     * @return A Flowable of OwnershipTransferredEventResponse objects.
     */
    public @NotNull Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(@NotNull EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> {
            final EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
            final OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = log;
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            return typedResponse;
        });
    }

    /**
     * This function returns a Flowable of OwnershipTransferredEventResponse objects,
     * which are emitted when the OwnershipTransferred event is triggered
     *
     * @param startBlock The block number to start listening from.
     * @param endBlock The block number to stop getting logs from (inclusive).
     * @return A Flowable of OwnershipTransferredEventResponse objects.
     */
    public @NotNull Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(@NotNull DefaultBlockParameter startBlock,
                                                                                                  @NotNull DefaultBlockParameter endBlock) {
        final EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    /**
     * It takes a transaction receipt and returns a list of TransferEventResponse objects
     *
     * @param transactionReceipt The transaction receipt of the transaction that triggered the event.
     * @return A list of TransferEventResponse objects.
     */
    public @NotNull List<TransferEventResponse> getTransferEvents(@NotNull TransactionReceipt transactionReceipt) {
        final List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        final ArrayList<TransferEventResponse> responses = new ArrayList<>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            final TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    /**
     * This function returns a Flowable that emits TransferEventResponse objects,
     * which are created by extracting the event parameters from the logs of the Transfer event.
     *
     * @param filter The filter object that will be used to filter the events.
     * @return A Flowable of TransferEventResponse objects.
     */
    public @NotNull Flowable<TransferEventResponse> transferEventFlowable(@NotNull EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> {
            final EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
            final TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = log;
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            return typedResponse;
        });
    }

    /**
     * This function returns a Flowable of TransferEventResponse objects,
     * which are emitted when the Transfer event is triggered
     *
     * @param startBlock The block number to start listening from.
     * @param endBlock The block number to stop getting logs on.
     * @return A Flowable of TransferEventResponse objects.
     */
    public @NotNull Flowable<TransferEventResponse> transferEventFlowable(@NotNull DefaultBlockParameter startBlock,
                                                                          @NotNull DefaultBlockParameter endBlock) {
        final EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    /**
     * It returns a list of TransferRefEventResponse objects,
     * which are the responses to the TransferRefEvent event
     *
     * @param transactionReceipt The transaction receipt of the transaction that triggered the event.
     * @return A list of TransferRefEventResponse objects.
     */
    public @NotNull List<TransferRefEventResponse> getTransferRefEvents(@NotNull TransactionReceipt transactionReceipt) {
        final List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERREF_EVENT, transactionReceipt);
        final ArrayList<TransferRefEventResponse> responses = new ArrayList<>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            final TransferRefEventResponse typedResponse = new TransferRefEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.recipient = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.ref = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    /**
     * It returns a Flowable that emits TransferRefEventResponse objects,
     * which are created by extracting the event parameters from the log
     *
     * @param filter The filter object that will be used to filter the events.
     * @return A Flowable of TransferRefEventResponse
     */
    public @NotNull Flowable<TransferRefEventResponse> transferRefEventFlowable(@NotNull EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> {
            final EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERREF_EVENT, log);
            final TransferRefEventResponse typedResponse = new TransferRefEventResponse();
            typedResponse.log = log;
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.recipient = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.ref = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            return typedResponse;
        });
    }

    /**
     * This function returns a Flowable of TransferRefEventResponse objects,
     * which are emitted when the TransferRef event is triggered
     *
     * @param startBlock The block number to start listening from.
     * @param endBlock The block number to stop getting logs.
     * @return A Flowable of TransferRefEventResponse objects.
     */
    public @NotNull Flowable<TransferRefEventResponse> transferRefEventFlowable(@NotNull DefaultBlockParameter startBlock,
                                                                                @NotNull DefaultBlockParameter endBlock) {
        final EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERREF_EVENT));
        return transferRefEventFlowable(filter);
    }

    /**
     * `allowance` is a function that takes two arguments, `owner` and `spender`, and returns a `BigInteger` value
     *
     * @param owner The address of the owner of the token
     * @param spender The address of the account allowed to spend the funds.
     * @return The amount of tokens that spender is allowed to spend on behalf of owner.
     */
    public @NotNull RemoteFunctionCall<BigInteger> allowance(@NotNull String owner, @NotNull String spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, owner),
                new org.web3j.abi.datatypes.Address(160, spender)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `approve` is a function that takes two arguments, `spender` and `amount`, and returns a
     * `RemoteFunctionCall<TransactionReceipt>` object
     *
     * @param spender The address of the account able to transfer the tokens
     * @param amount The amount of tokens to approve.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> approve(@NotNull String spender, @NotNull BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, spender),
                new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `balanceOf` is a function that takes an account address as a parameter and returns the balance of that account
     *
     * @param account The address of the account to query the balance of.
     * @return The balance of the account.
     */
    public @NotNull RemoteFunctionCall<BigInteger> balanceOf(@NotNull String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF,
                List.of(new Address(160, account)),
                List.of(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `cap()` returns the `cap` of the contract
     *
     * @return The cap of the contract.
     */
    public @NotNull RemoteFunctionCall<BigInteger> cap() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CAP,
                List.of(),
                List.of(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `decimals()` is a function that returns a `BigInteger` value
     *
     * @return The number of decimals of the token.
     */
    public @NotNull RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS,
                List.of(),
                List.of(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `decreaseAllowance` is a function that takes two arguments, `spender` and `subtractedValue`, and returns a
     * `RemoteFunctionCall<TransactionReceipt>` object
     *
     * @param spender The address of the account able to transfer the tokens.
     * @param subtractedValue The amount of tokens to decrease the allowance by.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> decreaseAllowance(@NotNull String spender,
                                                                             @NotNull BigInteger subtractedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DECREASEALLOWANCE,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, spender),
                new org.web3j.abi.datatypes.generated.Uint256(subtractedValue)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * This function takes in a user address and deposit data, and returns a transaction receipt.
     *
     * @param user The address of the user who is depositing.
     * @param depositData This is the data that is being sent to the contract.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> deposit(@NotNull String user, byte[] depositData) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSIT,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, user),
                new org.web3j.abi.datatypes.DynamicBytes(depositData)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `feeBps()` returns the fee in basis points (1/100th of a percent) that the contract charges for each transaction
     *
     * @return The feeBps() function returns the feeBps value.
     */
    public @NotNull RemoteFunctionCall<BigInteger> feeBps() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FEEBPS,
                List.of(),
                List.of(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `feeCap()` returns the fee cap of the current auction
     *
     * @return The fee cap of the contract.
     */
    public @NotNull RemoteFunctionCall<BigInteger> feeCap() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FEECAP,
                List.of(),
                List.of(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `feeFixed()` returns the fixed fee for the contract
     *
     * @return The feeFixed() function is being returned.
     */
    public @NotNull RemoteFunctionCall<BigInteger> feeFixed() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FEEFIXED,
                List.of(),
                List.of(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `increaseAllowance` is a function that takes two arguments, `spender` and `addedValue`, and returns a
     * `RemoteFunctionCall<TransactionReceipt>` object
     *
     * @param spender The address of the account able to transfer the tokens.
     * @param addedValue The amount of tokens to increase the allowance by.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> increaseAllowance(@NotNull String spender,
                                                                             @NotNull BigInteger addedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INCREASEALLOWANCE,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, spender),
                new org.web3j.abi.datatypes.generated.Uint256(addedValue)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `name()` is a function that returns a string
     *
     * @return The name of the contract
     */
    public @NotNull RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME,
                List.of(),
                List.of(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    /**
     * `RemoteFunctionCall<String> symbol()`
     *
     * This function returns a `RemoteFunctionCall<String>` object
     *
     * @return The symbol of the token.
     */
    public @NotNull RemoteFunctionCall<String> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SYMBOL,
                List.of(),
                List.of(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    /**
     * `totalSupply()` is a function that returns the total supply of the token
     *
     * @return The total supply of the token.
     */
    public @NotNull RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY,
                List.of(),
                List.of(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    /**
     * `transfer` is a function that takes two arguments, `recipient` and `amount`, and returns a
     * `RemoteFunctionCall<TransactionReceipt>` object
     *
     * @param recipient The address of the recipient of the transfer.
     * @param amount The amount of tokens to be transferred
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> transfer(@NotNull String recipient, @NotNull BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, recipient),
                new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `transferFrom` is a function that takes in three parameters: `sender`, `recipient`, and `amount`. It returns a
     * `RemoteFunctionCall<TransactionReceipt>` object
     *
     * @param sender The address of the account that is sending the tokens.
     * @param recipient The address of the recipient of the tokens.
     * @param amount The amount of tokens to be transferred.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> transferFrom(@NotNull String sender,
                                                                        @NotNull String recipient,
                                                                        @NotNull BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, sender),
                new org.web3j.abi.datatypes.Address(160, recipient),
                new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `transferWithFee` is a function that takes two arguments, a recipient address and an amount, and returns a
     * transaction receipt
     *
     * @param recipient The address of the recipient of the transfer.
     * @param amount The amount of tokens to transfer.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> transferWithFee(@NotNull String recipient,
                                                                           @NotNull BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERWITHFEE,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, recipient),
                new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `transferWithFeeRef` is a function that takes in a recipient address, an amount, and a reference, and returns a
     * transaction receipt
     *
     * @param recipient The address of the recipient of the transfer.
     * @param amount The amount of tokens to transfer.
     * @param ref The reference number of the transaction.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> transferWithFeeRef(@NotNull String recipient,
                                                                              @NotNull BigInteger amount,
                                                                              @NotNull BigInteger ref) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERWITHFEEREF,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, recipient),
                new org.web3j.abi.datatypes.generated.Uint256(amount),
                new org.web3j.abi.datatypes.generated.Uint256(ref)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * `transferWithRef` is a function that takes in a recipient address, an amount, and a reference number, and returns a
     * transaction receipt
     *
     * @param recipient The address of the recipient of the transfer.
     * @param amount The amount of tokens to transfer.
     * @param ref The reference number of the transaction.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> transferWithRef(@NotNull String recipient,
                                                                           @NotNull BigInteger amount,
                                                                           @NotNull BigInteger ref) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERWITHREF,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, recipient),
                new org.web3j.abi.datatypes.generated.Uint256(amount),
                new org.web3j.abi.datatypes.generated.Uint256(ref)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * This function withdraws the amount of ether specified by the amount parameter from the contract and sends it to the
     * address that called the function.
     *
     * @param amount The amount of tokens to withdraw.
     * @return A RemoteFunctionCall object.
     */
    public @NotNull RemoteFunctionCall<TransactionReceipt> withdraw(@NotNull BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW,
                List.of(new Uint256(amount)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static @NotNull PolygonWRLDToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PolygonWRLDToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static @NotNull PolygonWRLDToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PolygonWRLDToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static @NotNull PolygonWRLDToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PolygonWRLDToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static @NotNull PolygonWRLDToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PolygonWRLDToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }

    public static class TransferRefEventResponse extends BaseEventResponse {
        public String sender;

        public String recipient;

        public BigInteger amount;

        public BigInteger ref;
    }
}
