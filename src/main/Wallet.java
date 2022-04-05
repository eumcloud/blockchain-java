package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.GsonBuilder;
import util.Config;
import util.FileCtl;
import util.FileLog;
import util.RSAKey;


/*
- 클래스명 : Wallet
- 설      명 : 전자지갑 생성, 거래 생성
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.01
*/

public class Wallet {
	
	public String owner;
	public String privateKey;
	public String publicKey;
	
	private static String PRIAVTE_KEY_FILE_EXTENTION = Config.PRIAVTE_KEY_FILE_EXTENTION;
	private static String PUBLIC_KEY_FILE_EXTENTION = Config.PUBLIC_KEY_FILE_EXTENTION;
	
	public Wallet( String wallnetName){
		this.owner = wallnetName;
		
		String strPivFile = this.owner + PRIAVTE_KEY_FILE_EXTENTION;
		String strPubFile = this.owner + PUBLIC_KEY_FILE_EXTENTION;
		
		this.publicKey = FileCtl.readFile(strPubFile);
		this.privateKey = FileCtl.readFile(strPivFile);
		   
		if (this.publicKey != null && this.privateKey != null ) return;
		   
		RSAKey key = new RSAKey();
		this.publicKey = key.publicKey;
		this.privateKey = key.privateKey;
		
		FileCtl.writeFile(strPivFile, privateKey, false);
	    FileCtl.writeFile(strPubFile, publicKey, false);
	}
	
	//전자지갑 소유의 전체 잔고 가져오기
	public float getBalance() {
		float total = 0;
		Block currentBlock; 
		
		//블록체인 잔고
		for(int i=0; i < Blockchain.blockchain.size(); i++) {
			currentBlock = Blockchain.blockchain.get(i);
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				//블록체인의 모든 블록을 읽어서 블록 내에 트랜잭션 목록에서 전자지갑 공개키 값이 수신자로 되어 있으면 값을 더함
				if (currentBlock.transactions.get(t).reciepient.equals(publicKey)) total += currentBlock.transactions.get(t).value;
				//블록체인의 모든 블록을 읽어서 블록 내에 트랜잭션 목록에서 전자지갑 공개키 값이 송신자로 되어 있으면 값을 뺌
				if (currentBlock.transactions.get(t).sender.equals(publicKey)) total -= currentBlock.transactions.get(t).value;
			}
	    }  
		
		//맴풀 잔고
		for(int t=0; t <Mempool.transactions.size(); t++) {
			//맴풀의 트랜잭션 목록에서 전자지갑 공개키 값이 수신자로 되어 있으면 값을 더함
			if (Mempool.transactions.get(t).reciepient.equals(publicKey)) total += Mempool.transactions.get(t).value;
			//맴풀의 트랜잭션 목록에서 전자지갑 공개키 값이 송신자로 되어 있으면 값을 뺌
			if (Mempool.transactions.get(t).sender.equals(publicKey)) total -= Mempool.transactions.get(t).value;
		}
		
		return total;
	}
	
	//전자지갑 소유의 모든 입력 잔고 값 가져오기
	public float getInputBalance() {
		float total = 0;
		Block currentBlock; 
		
		//블록체인 잔고
		for(int i=0; i < Blockchain.blockchain.size(); i++) {
			currentBlock = Blockchain.blockchain.get(i);
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				//블록체인의 모든 블록을 읽어서 블록 내에 트랜잭션 목록에서 전자지갑 공개키 값이 송신자로 되어 있으면 값을 더함
				if (currentBlock.transactions.get(t).sender.equals(publicKey)) total += currentBlock.transactions.get(t).value;
			}
	    }  
		
		//맴풀 잔고
		for(int t=0; t <Mempool.transactions.size(); t++) {
			//맴풀의 트랜잭션 목록에서 전자지갑 공개키 값이 송신자로 되어 있으면 값을 더함
			if (Mempool.transactions.get(t).sender.equals(publicKey)) total += Mempool.transactions.get(t).value;
		}
		
		return total;
	}
	
	//전자지값 소유의 모든 출력 잔고 값 가져오기
	public float getOutputBalance() {
		float total = 0;
		Block currentBlock; 
		
		//블록체인 잔고
		for(int i=0; i < Blockchain.blockchain.size(); i++) {
			currentBlock = Blockchain.blockchain.get(i);
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				//블록체인의 모든 블록을 읽어서 블록 내에 트랜잭션 목록에서 전자지갑 공개키 값이 수신자로 되어 있으면 값을 더함
				if (currentBlock.transactions.get(t).reciepient.equals(publicKey)) total += currentBlock.transactions.get(t).value;
			}
	    }  
		
		//맴풀 잔고
		for(int t=0; t <Mempool.transactions.size(); t++) {
			//블록체인의 모든 블록을 읽어서 블록 내에 트랜잭션 목록에서 전자지갑 공개키 값이 수신자로 되어 있으면 값을 더함
			if (Mempool.transactions.get(t).reciepient.equals(publicKey)) total += Mempool.transactions.get(t).value;
		}
		
		return total;
	}
		
	//Generates and returns a new transaction from this wallet.
	public void sendFunds(String _recipient, float value) {
		float getBalance =  getBalance();
		if(getBalance < value) { //gather balance and check funds.
			FileLog.writeErrorLog("거래잔고 : " + getBalance + " 거래금액 : " + value + " => 거래 잔고가 부족하여 이 거래는 버려집니다");
			//System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return;
		}
		
		//거래 트랜잭션 배열 생성 및 초기화
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		//UTXO 트랜잭션에 추가
		this.addUtxo();
		
		//String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson(StudentUtxo.UTXOs);
		//System.out.println("UTXOs is /n" + jsonOutput);
		
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item:TransactionUtxo.UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			
			//System.out.println("UTXO.value is " + UTXO.value);
			//System.out.println("UTXO.id is " + UTXO.id);
			//System.out.println("total is " + total);
			inputs.add(new TransactionInput(UTXO.id));
			
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		/*
		for(StudentTransactionInput input: inputs){
			StudentUtxo.UTXOs.remove(input.transactionOutputId);
		}
		*/
		if(newTransaction.processTransaction() != true) {
			System.out.println("Transaction failed to process. Discarded.");
			return;
		}
		
		Mempool.transactions.add(newTransaction);
		TransactionUtxo.UTXOs = new HashMap<String, TransactionOutput>();  //UTXO Init
		//write mempool
		Mempool.writeMempool(Mempool.transactions);
		
		//System.out.println("Transaction writes Mempool.");
		
	}
		
	public void addUtxo () {
		try {
			
			Block currentBlock;
			HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
			
			//blockchain utxo add
			for(int i=0; i < Blockchain.blockchain.size(); i++) {
				
				currentBlock = Blockchain.blockchain.get(i);
				for(int t=0; t <currentBlock.transactions.size(); t++) {
					Transaction currentTransaction = currentBlock.transactions.get(t);
					
					for (int u=0; u < currentTransaction.outputs.size(); u++) {
						if (currentTransaction.outputs.get(u).reciepient.equals(publicKey)) {
							TransactionUtxo.addUtxo(currentTransaction.outputs.get(u).id, currentTransaction.outputs.get(u));
						}
					}
					
					String transactonOutputId = "";
					if (currentTransaction.inputs.size() > 0) {
						transactonOutputId = currentTransaction.inputs.get(0).transactionOutputId;
					}
					//사용된 코인은 UTXO에서 제거
					//System.out.println("UTXOs is " + StudentUtxo.UTXOs);
					for( Map.Entry<String, TransactionOutput> elem :TransactionUtxo.UTXOs.entrySet() ){
						if (!elem.getKey().equals(transactonOutputId)) {
							tempUTXOs.put(elem.getKey(), elem.getValue());
						}
			        }
				}
			}
			
			//System.out.println("UTXOs mempool is " + StudentUtxo.UTXOs);
			
			for(int t=0; t <Mempool.transactions.size(); t++) {
				Transaction currentTransaction = Mempool.transactions.get(t);
				for (int u=0; u < currentTransaction.outputs.size(); u++) {
					if (currentTransaction.outputs.get(u).reciepient.equals(publicKey)) {
						TransactionUtxo.addUtxo(currentTransaction.outputs.get(u).id, currentTransaction.outputs.get(u));
					}
				}
				
				String transactonOutputId = "";
				if (currentTransaction.inputs.size() > 0) {
					transactonOutputId = currentTransaction.inputs.get(0).transactionOutputId;
				}
				
				//사용된 코인은 UTXO에서 제거
				for( Map.Entry<String, TransactionOutput> elem :TransactionUtxo.UTXOs.entrySet() ){
					if (!elem.getKey().equals(transactonOutputId)) {
						tempUTXOs.put(elem.getKey(), elem.getValue());
					}
		        }
			}
			
			TransactionUtxo.UTXOs = tempUTXOs;
			
		} catch(Exception e) {
			FileLog.writeErrorLog(e.getMessage());
			throw new RuntimeException(e);
		}
	//System.out.println("UTXOs is " + TransactionUtxo.UTXOs);
	}
}