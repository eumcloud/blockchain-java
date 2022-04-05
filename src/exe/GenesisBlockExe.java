package exe;

import main.Blockchain;
import main.Wallet;

public class GenesisBlockExe {
    public static void main(String[] args)  {	
		
		Wallet walletA = new Wallet("A");
		Blockchain.addGenesisBlock(walletA.publicKey, 3000f,  "Winsoltek");
		if (Blockchain.isBlockchainValid()) {
			System.out.println("\nSaved Blockchain is valid");
		}
	}
}
