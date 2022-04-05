package exe;

import main.Blockchain;
import main.Mempool;
import main.Wallet;
import util.FileLog;

public class WalletExe {
	public static void main(String[] args)  {	
	
		
		//blockchain read
		Blockchain.readBlockchain();
		
		//mempool read
		Mempool.readMempool();
		 
		
		//제네시스 블록응 생성하면서 A에게 3000f 가 주어짐
		Wallet walletA = new Wallet("A");
		Wallet walletB = new Wallet("B");		
		Wallet walletC = new Wallet("C");
		
		walletA.sendFunds(walletB.publicKey, 10f);
		walletA.sendFunds(walletC.publicKey, 100f);
		walletB.sendFunds(walletC.publicKey, 10f);
				
		
		System.out.println("\nWallet A");
		System.out.println("Wallet A Total Output Ballance = " + walletA.getOutputBalance() );
		System.out.println("Wallet A Total Input Ballance = " + walletA.getInputBalance() );
		System.out.println("Wallet A Current Ballance = " + walletA.getBalance() );
		System.out.println("\nWallet B");
		System.out.println("Wallet B Total Output Ballance = " + walletB.getOutputBalance() );
		System.out.println("Wallet B Total Input Ballance = " + walletB.getInputBalance() );
		System.out.println("Wallet B Current Ballance = " + walletB.getBalance() );
		System.out.println("\nWallet C");
		System.out.println("Wallet C Total Output Ballance = " + walletC.getOutputBalance() );
		System.out.println("Wallet C Total Input Ballance = " + walletC.getInputBalance() );
		System.out.println("Wallet C Current Ballance = " + walletC.getBalance() );
		
		FileLog.writeDebugLog( "gkgkgkgk" );
	}
}
