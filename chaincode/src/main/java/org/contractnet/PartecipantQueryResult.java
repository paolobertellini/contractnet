/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * PartecipantQueryResult structure used for handling result of query
 *
 */
@DataType()
public final class PartecipantQueryResult {
    @Property()
    private final String key;

    @Property()
    private final Partecipant record;

    public PartecipantQueryResult(@JsonProperty("Key") final String key, @JsonProperty("Record") final Partecipant record) {
        this.key = key;
        this.record = record;
    }

    public String getKey() {
        return key;
    }

    public Partecipant getRecord() {
        return record;
    }

    public int getOffer() { return record.getOffer(); }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        PartecipantQueryResult other = (PartecipantQueryResult) obj;

        Boolean recordsAreEquals = this.getRecord().equals(other.getRecord());
        Boolean keysAreEquals = this.getKey().equals(other.getKey());

        return recordsAreEquals && keysAreEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey(), this.getRecord());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [key=" + key + ", record="
                + record + "]";
    }
}
