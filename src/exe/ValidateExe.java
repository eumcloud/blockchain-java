package exe;

import main.Blockchain;

public class ValidateExe {
	public static void main(String[] args)  {	
		//blockchain read
		Blockchain.readBlockchain();
						
		if (Blockchain.isBlockchainValid()) {
			System.out.println("\nBlockchain is valid");
		}
	}
}
