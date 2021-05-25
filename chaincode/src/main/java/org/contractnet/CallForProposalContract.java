/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java implementation of the Fabric CallForProposal Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "Contractnet",
        info = @Info(
                title = "Contractnet protocol",
                description = "Contractnet protocol",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
@Default
public final class CallForProposalContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FabCallForProposalErrors {
        CallForProposal_NOT_FOUND,
        CallForProposal_ALREADY_EXISTS,
        Partecipant_NOT_FOUND,
        Partecipant_ALREADY_EXISTS
    }

     @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<CallForProposal> calls = new ArrayList<>();
        calls.add(new CallForProposal("Paul", "Paint car", CallForProposal.CREATED, new ArrayList<String>()));
        calls.add(new CallForProposal("John", "Find best path", CallForProposal.CREATED, new ArrayList<String>()));
        calls.add(new CallForProposal("Frank", "Generate random numbers", CallForProposal.CREATED, new ArrayList<String>()));

        List<Partecipant> partecipants = new ArrayList<>();
        partecipants.add(new Partecipant("partecipant1", Partecipant.WAITING, -1));
        partecipants.add(new Partecipant("partecipant2", Partecipant.WAITING, -1));
        partecipants.add(new Partecipant("partecipant3", Partecipant.WAITING, -1));

        for (int i = 0; i < calls.size(); i++) {
            String key = String.format("cfp%d", i+1);
            String callForProposalState = genson.serialize(calls.get(i));
            stub.putStringState(key, callForProposalState);
        }

        for (int i = 0; i < partecipants.size(); i++) {
            String key = String.format("prt%d", i+1);
            String partecipantState = genson.serialize(partecipants.get(i));
            stub.putStringState(key, partecipantState);
        }

    }


    /* Create */

    @Transaction()
    public CallForProposal createCallForProposal(final Context ctx, final String key, final String initiator, final String task,
                                                 final String state) {
        ChaincodeStub stub = ctx.getStub();

        String callForProposalState = stub.getStringState(key);
        if (!callForProposalState.isEmpty()) {
            String errorMessage = String.format("CallForProposal %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.CallForProposal_ALREADY_EXISTS.toString());
        }

        CallForProposal callForProposal = new CallForProposal(initiator, task, state, new ArrayList<String>());
        callForProposalState = genson.serialize(callForProposal);
        stub.putStringState(key, callForProposalState);

        return callForProposal;
    }
    @Transaction()
    public Partecipant createPartecipant(final Context ctx, final String key, final String name) {
        ChaincodeStub stub = ctx.getStub();

        String partecipantState = stub.getStringState(key);
        if (!partecipantState.isEmpty()) {
            String errorMessage = String.format("Partecipant %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.Partecipant_ALREADY_EXISTS.toString());
        }

        Partecipant partecipant = new Partecipant(name, Partecipant.WAITING, -1);
        partecipantState = genson.serialize(partecipant);
        stub.putStringState(key, partecipantState);

        return partecipant;
    }

    /* Update */

    public CallForProposal updateCallForProposal(final Context ctx, final String key, final String initiator, final String task, final String state, final List<String> partecipants) {
        CallForProposal newCallForProposal = new CallForProposal(initiator, task, state, partecipants);
        String newCallForProposalState = genson.serialize(newCallForProposal);
        ctx.getStub().putStringState(key, newCallForProposalState);
        return newCallForProposal;
    }

    public Partecipant updatePartecipant(final Context ctx, final String key, final String name, final String state, final int offer) {
        Partecipant newPartecipant = new Partecipant(name, state, offer);
        String newPartecipantState = genson.serialize(newPartecipant);
        ctx.getStub().putStringState(key, newPartecipantState);
        return newPartecipant;
    }

    /* Query */

    public CallForProposal getCallForProposal(final Context ctx, final String key){
        ChaincodeStub stub = ctx.getStub();
        String callForProposalState = stub.getStringState(key);
        if (callForProposalState.isEmpty()) {
            String errorMessage = String.format("CallForProposal %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.CallForProposal_NOT_FOUND.toString());
        }
        CallForProposal callForProposal = genson.deserialize(callForProposalState, CallForProposal.class);
        return callForProposal;
    }

    public Partecipant getPartecipant(final Context ctx, final String key){
        ChaincodeStub stub = ctx.getStub();
        String partecipantState = stub.getStringState(key);
        if (partecipantState.isEmpty()) {
            String errorMessage = String.format("Partecipant %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.Partecipant_NOT_FOUND.toString());
        }
        Partecipant partecipant = genson.deserialize(partecipantState, Partecipant.class);
        return partecipant;
    }

    @Transaction()
    public String queryAllCallForProposals(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "cfp1";
        final String endKey = "cfp99";
        List<CallForProposalQueryResult> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            CallForProposal callForProposal = genson.deserialize(result.getStringValue(), CallForProposal.class);
            queryResults.add(new CallForProposalQueryResult(result.getKey(), callForProposal));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    @Transaction()
    public String queryAllPartecipants(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "prt1";
        final String endKey = "prt99";
        List<PartecipantQueryResult> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Partecipant partecipant = genson.deserialize(result.getStringValue(), Partecipant.class);
            queryResults.add(new PartecipantQueryResult(result.getKey(), partecipant));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }


    /* Operations */

    @Transaction()
    public String callAllPartecipants(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, key);

        final String startKey = "prt1";
        final String endKey = "prt99";

        List<PartecipantQueryResult> queryResults = new ArrayList<>();
        List<String> partecipants = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Partecipant partecipant = getPartecipant(ctx, result.getKey());
            if (partecipant.isWaiting()) {
                partecipants.add(result.getKey());
                updatePartecipant(ctx, result.getKey(), partecipant.getName(), Partecipant.CALLED, -1);
            }
        }

        updateCallForProposal(ctx, key, callForProposal.getInitiator(), callForProposal.getTask(), CallForProposal.CALLING, partecipants);
        return partecipants.toString();
    }

    @Transaction()
    public String refuseCallForProposal(final Context ctx, final String partecipantKey, final String cfpKey) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);
        Partecipant partecipant = getPartecipant(ctx, partecipantKey);

        if (!callForProposal.isCalling()) {
            String errorMessage = String.format("Call for proposal %s is not calling", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        updatePartecipant(ctx, partecipantKey, partecipant.getName(), Partecipant.REFUSED, -1);

        List<String> partecipants = callForProposal.getPartecipants();
        partecipants.remove(partecipantKey);

        updateCallForProposal(ctx, cfpKey, callForProposal.getInitiator(), callForProposal.getTask(), CallForProposal.CALLING, partecipants);

        return partecipants.toString();
    }

    @Transaction()
    public String proposeForCallForProposal(final Context ctx, final String partecipantKey, final String cfpKey, final int offer) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);
        Partecipant partecipant = getPartecipant(ctx, partecipantKey);

        if (!callForProposal.isCalling()) {
            String errorMessage = String.format("Call for proposal %s is not calling", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        updatePartecipant(ctx, partecipantKey, partecipant.getName(), Partecipant.PROPOSED, offer);
        List<String> partecipants = callForProposal.getPartecipants();
        updateCallForProposal(ctx, cfpKey, callForProposal.getInitiator(), callForProposal.getTask(), CallForProposal.CALLING, partecipants);

        return partecipants.toString();
    }

    @Transaction()
    public String callForProposalResult(final Context ctx, final String winnerKey) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "prt1";
        final String endKey = "prt99";
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result : results) {
            Partecipant partecipant = getPartecipant(ctx, result.getKey());
            Partecipant newPartecipant;
            if (result.getKey().equals(winnerKey)) {
                updatePartecipant(ctx, result.getKey(), partecipant.getName(), Partecipant.WORKING, partecipant.getOffer());
            }
            else {
                updatePartecipant(ctx, result.getKey(), partecipant.getName(), Partecipant.WAITING, -1);
            }
        }
        return genson.serialize(results);
    }

    @Transaction()
    public String closeCallForProposal(final Context ctx, final String cfpKey) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);

        if (!callForProposal.isCalling()) {
            String errorMessage = String.format("Call for proposal %s is not calling", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        List<PartecipantQueryResult> proposed = new ArrayList<>();
        for(String key: callForProposal.getPartecipants()){
            Partecipant partecipant = getPartecipant(ctx, key);
            if(partecipant.isProposed()){
                proposed.add(new PartecipantQueryResult(key, partecipant));
            }
            updatePartecipant(ctx, key, partecipant.getName(), Partecipant.WAITING, -1);
        }

        PartecipantQueryResult winner = Collections.max(proposed, Comparator.comparingInt(PartecipantQueryResult::getOffer));
        callForProposalResult(ctx, winner.getKey());
        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(), callForProposal.getTask(),CallForProposal.WORKING, callForProposal.getPartecipants());
        newCallForProposal.setWinner(winner.getKey());
        String newCallForProposalState = genson.serialize(newCallForProposal);
        ctx.getStub().putStringState(cfpKey, newCallForProposalState);

        return winner.getKey();

    }

    @Transaction()
    public String endCallForProposal(final Context ctx, final String cfpKey) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);

        if (!callForProposal.isWorking()) {
            String errorMessage = String.format("Call for proposal %s is not working", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        Partecipant partecipant = getPartecipant(ctx, callForProposal.getWinner());
        updatePartecipant(ctx, callForProposal.getWinner(), partecipant.getName(), Partecipant.WAITING, -1);
        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(), callForProposal.getTask(),CallForProposal.ENDED, callForProposal.getPartecipants());
        newCallForProposal.setWinner(callForProposal.getWinner());
        String newCallForProposalState = genson.serialize(newCallForProposal);
        ctx.getStub().putStringState(cfpKey, newCallForProposalState);

        return "Call for proposal ended";
    }
}

