/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONPropertyIgnore;

import java.util.Objects;

@DataType()
public final class Partecipant {

    public final static String WAITING = "WAITING";
    public final static String CALLED = "CALLED";
    public final static String REFUSED = "REFUSED";
    public final static String PROPOSED = "PROPOSED";
    public final static String WORKING = "WORKING";

    @Property()
    private String state = "";

    @JSONPropertyIgnore()
    public boolean isWaiting() {
        return this.state.equals(Partecipant.WAITING);
    }

    @JSONPropertyIgnore()
    public boolean isCalled() {
        return this.state.equals(Partecipant.CALLED);
    }

    @JSONPropertyIgnore()
    public boolean isRefused() {
        return this.state.equals(Partecipant.REFUSED);
    }

    @JSONPropertyIgnore()
    public boolean isProposed() {
        return this.state.equals(Partecipant.PROPOSED);
    }

    @JSONPropertyIgnore()
    public boolean isWorking() {
        return this.state.equals(Partecipant.WORKING);
    }

    public Partecipant setWaiting() {
        this.state = Partecipant.WAITING;
        return this;
    }

    public Partecipant setCalled() {
        this.state = Partecipant.CALLED;
        return this;
    }

    public Partecipant setRefused() {
        this.state = Partecipant.REFUSED;
        return this;
    }

    public Partecipant setProposed() {
        this.state = Partecipant.PROPOSED;
        return this;
    }

    public Partecipant setWorking() {
        this.state = Partecipant.WORKING;
        return this;
    }

    @Property()
    private final String name;

    @Property()
    private int offer;

    public String getName() {
        return name;
    }

    public int getOffer() {
        return offer;
    }

    public String getState() {
        return state;
    }

    public Partecipant setOffer(int offer) {
        this.offer = offer;
        return this;
    }

    public Partecipant(@JsonProperty("name") final String name, @JsonProperty("state") final String state,
            @JsonProperty("offer") final int offer) {
        this.name = name;
        this.state = state;
        this.offer = offer;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Partecipant other = (Partecipant) obj;

        return Objects.deepEquals(new String[]{getName(), getState()},
                new String[]{other.getName(), other.getState()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getState());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [name=" + name + ", state=" + state + "]";
    }
}
