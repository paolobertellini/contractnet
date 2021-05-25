/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;


public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		try (Gateway gateway = connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("contractnet");

			byte[] result;

			System.out.println("Initializing ledger with 3 call for proposals and 3 partecipants");
			result = contract.submitTransaction("initLedger");
			System.out.println(new String(result));

			System.out.println("Calling all partecipants for CFP1");
			result = contract.submitTransaction("callAllPartecipants", "cfp1");
			System.out.println(new String(result));

			System.out.println("Partecipant PRT1 refuse CFP1");
			result = contract.submitTransaction("refuseCallForProposal", "prt1", "cfp1");
			System.out.println(new String(result));

			System.out.println("Partecipant PRT2 proposing to CFP1 with offer: 60");
			result = contract.submitTransaction("proposeForCallForProposal", "prt2", "cfp1", "60");
			System.out.println(new String(result));

			System.out.println("Partecipant PRT3 proposing to CFP1 with offer: 30");
			result = contract.submitTransaction("proposeForCallForProposal", "prt3", "cfp1", "30");
			System.out.println(new String(result));

			System.out.println("Closing CFP1");
			result = contract.submitTransaction("closeCallForProposal", "cfp1");
			System.out.println(new String(result));

			System.out.println("Ending CFP1");
			result = contract.submitTransaction("endCallForProposal", "cfp1");
			System.out.println(new String(result));

			System.out.println("\n");
			System.out.println("Ledger state");
			result = contract.evaluateTransaction("queryAllCallForProposals");
			System.out.println("result: " + new String(result));
		}
		catch(Exception e){
			System.err.println(e);
		}

	}
}
