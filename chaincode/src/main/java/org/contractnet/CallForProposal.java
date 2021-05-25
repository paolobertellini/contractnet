/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONPropertyIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DataType()
public final class CallForProposal {

    public final static String CREATED = "CREATED";
    public final static String CALLING = "CALLING";
    public final static String WORKING = "WORKING";
    public final static String ENDED = "ENDED";

    @Property()
    private String state="";

    @JSONPropertyIgnore()
    public boolean isCreated() {
        return this.state.equals(CallForProposal.CREATED);
    }

    @JSONPropertyIgnore()
    public boolean isCalling() {
        return this.state.equals(CallForProposal.CALLING);
    }

    @JSONPropertyIgnore()
    public boolean isWorking() {
        return this.state.equals(CallForProposal.WORKING);
    }

    @JSONPropertyIgnore()
    public boolean isEnded() {
        return this.state.equals(CallForProposal.ENDED);
    }

    public CallForProposal setCreated() {
        this.state = CallForProposal.CREATED;
        return this;
    }

    public CallForProposal setCalling() {
        this.state = CallForProposal.CALLING;
        return this;
    }

    public CallForProposal setWorking() {
        this.state = CallForProposal.WORKING;
        return this;
    }

    public CallForProposal setEnded() {
        this.state = CallForProposal.ENDED;
        return this;
    }

    @Property()
    private List<String> partecipants = new ArrayList<>();

    @Property()
    private final String initiator;

    @Property()
    private final String task;

    @Property()
    private String winner;

    public String getInitiator() {
        return initiator;
    }

    public String getTask() {
        return task;
    }

    public String getState() {
        return state;
    }

    public String getWinner() { return winner; }

    public void setWinner(String winner) { this.winner = winner; }

    public List<String> getPartecipants() {
        return partecipants;
    }


    public CallForProposal(@JsonProperty("initiator") final String initiator, @JsonProperty("task") final String task,
                           @JsonProperty("state") final String state, @JsonProperty("partecipants") final List<String> partecipants) {
        this.initiator = initiator;
        this.task = task;
        this.state = state;
        this.partecipants = partecipants;
        this.winner = "";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        org.contractnet.CallForProposal other = (org.contractnet.CallForProposal) obj;

        return Objects.deepEquals(new String[] {getInitiator(), getTask(), getState()},
                new String[] {other.getInitiator(), other.getTask(), other.getState()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInitiator(), getTask(), getState());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [initiator=" + initiator + ", task="
                + task + ", state=" + state + ", partecipants=" + partecipants + "]";
    }
}
