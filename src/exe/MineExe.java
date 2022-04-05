package exe;

import main.Blockchain;
import main.Mempool;


public class MineExe {
public static void main(String[] args)  {	
		
		//blockchain read
		Blockchain.readBlockchain();
		
		//mempool read
		Mempool.readMempool();
		
		Blockchain.mineBlock("Winsoltek");
		if (Blockchain.isBlockchainValid()) {
			System.out.println("\nSaved Blockchain is valid");
		}
	}
}
