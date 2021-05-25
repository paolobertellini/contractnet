/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * CallForProposalQueryResult structure used for handling result of query
 *
 */
@DataType()
public final class CallForProposalQueryResult {
    @Property()
    private final String key;

    @Property()
    private final CallForProposal record;

    public CallForProposalQueryResult(@JsonProperty("Key") final String key, @JsonProperty("Record") final CallForProposal record) {
        this.key = key;
        this.record = record;
    }

    public String getKey() {
        return key;
    }

    public CallForProposal getRecord() {
        return record;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        org.contractnet.CallForProposalQueryResult other = (org.contractnet.CallForProposalQueryResult) obj;

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
