package main;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import main.Block;
import util.Config;
import util.FileCtl;
import util.FileLog;
import main.Wallet;

/*
 - 클래스명 : Blockchain 
 - 설      명 : 블럭체인을 만들고, 브록체인을 검증함
 - 생 성  자 : BizAn 
 - 생 성  일 : 2020.07.02
 */

public class Blockchain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	private static int DIFFICULTY = Config.DIFFICULTY;
	private static String BLOCKCHAIN_FILE_NAME = Config.BLOCKCHAIN_FILE_NAME;
			
	public Blockchain() {	
		//add our blocks to the blockchain ArrayList:
		//Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
		//blockchain read
		//readBlockchain();
	}
	
	public static String addGenesisBlock(String targetWalletAddress, float genesisCoin, String minier) {
		//Create coinbase wallets:
		Wallet coinbase = new Wallet("coinbase");
		
		//create genesis transaction, which sends 3000 NoobCoin to walletA: 
		Transaction genesisTransaction = new Transaction(coinbase.publicKey, targetWalletAddress, genesisCoin, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		
		//UTXO 리스트에  트랜잭션 출력 등록
		TransactionUtxo.addUtxo(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
		Block block = new Block();
		block.addTransaction(genesisTransaction);
		addBlock(block, minier);
		
		return new GsonBuilder().setPrettyPrinting().create().toJson(block);
	}
	
	//블록체인에 블록 추가
	public static void addBlock(Block newBlock, String miner) {
		
		newBlock.mineBlock(DIFFICULTY);    //블록 채굴
		
		blockchain.add(newBlock);                  //블록체인 배열에 블록 추가
		newBlock.miner = miner;
		newBlock.height = blockchain.size();
		newBlock.transactionsCnt = newBlock.transactions.size();
		
		writeBlockchain(blockchain);         //블록체 파일에 저장
	}
	
	//블록 채굴
	public static String mineBlock(String miner) {
		
		Block block = new Block();
		
		if (!block.mineBlock2(DIFFICULTY)) return "";     //결과값이 false 면 채굴실패
		
		blockchain.add(block);
		block.miner = miner;
		block.height = blockchain.size();
		block.transactionsCnt = block.transactions.size();
		
		writeBlockchain(blockchain);                                               //블록체인 파일에 저장       
		
		Mempool.resetMempool();                                                    //멤풀 초기화
		Mempool.transactions = new ArrayList<Transaction>();    //트랜잭션 추기화 
		
		return new GsonBuilder().setPrettyPrinting().create().toJson(block);
	}
	
	//블록체인 검증
	public static Boolean isBlockchainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget ;
		
		if (blockchain == null || blockchain.size() == 0) {
			FileLog.writeErrorLog("검증할 블록체인 없음.");
			//System.out.println("no Blockchain");
			return false;
		}
		
		//주어진 블록 상태에서 사용되지 않은 트랜잭션의 임시 UTXO 작업 목록 배열 생성.
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); 
		
		//genesisBlock output 값 입력 
		tempUTXOs.put(blockchain.get(0).transactions.get(0).outputs.get(0).id, blockchain.get(0).transactions.get(0).outputs.get(0));
		
				
		//블록체인내  블록 무결성 검증.
		for(int i=1; i < blockchain.size(); i++) {
			
			hashTarget = new String(new char[blockchain.get(i).difficulty]).replace('\0', '0');
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			//저장된 블록 해시값과 계산한 해시값 비교 검증
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				FileLog.writeErrorLog("#Block(" + i + ")   현재 해시값과 일치하지 않음!");
				//System.out.println("#Block(" + i + ")   Current Hashes not equal");
				return false;
			}
			
			//저장된 이전 해시값과 현재 해시값 비교 검증
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				FileLog.writeErrorLog("#Block(" + i + ")   이전 해시값과 일치하지 않음!");
				//System.out.println("#Block(" + i + ")   Previous Hashes not equal");
				return false;
			}
						
			//해시가 정상 채굴되었는 지 확인
			if(!currentBlock.hash.substring( 0, DIFFICULTY).equals(hashTarget)) {
				FileLog.writeErrorLog("#Block(" + i + ")  이 블럭은 채굴되지 않았음!");
				//System.out.println("#Block(" + i + ")   This block hasn't been mined");
				return false;
			}
			
			//블록내 트랜잭션 무결성 검증:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifiySignature()) {
					FileLog.writeErrorLog("#Block(" + i + ")   #Transaction(" + t + ")  전자서명이 유효하지 않음!");
					//System.out.println("#Block(" + i + ")   Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					FileLog.writeErrorLog("#Block(" + i + ")   #Transaction(" + t + ")  입력이 트랜잭션의 출력과 같지 않음!");
					//System.out.println("#Block(" + i + ")   Inputs are not equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {
					
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					
					if(tempOutput == null) {
						//System.out.println("input.UTXO.value = " + new GsonBuilder().setPrettyPrinting().create().toJson(tempUTXOs));
						FileLog.writeErrorLog("#Block(" + i + ")   #Transaction(" + t + ")  에 대한 참조 입력이 없음!");
						//System.out.println("#Block(" + i + ")   Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						FileLog.writeErrorLog("#Block(" + i + ")   #Transaction(" + t + ")  의 참조된 입력 값이 잘못됨!");
						//System.out.println("#Block(" + i + ")   Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					//tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( !(currentTransaction.outputs.get(0).reciepient.equals(currentTransaction.reciepient))) {
					FileLog.writeErrorLog("#Block(" + i + ")   #Transaction(" + t + ")  의 참조된 입력 값이 잘못됨!");
					System.out.println("#Block(" + i + ")   #Transaction(" + t + ")  출력 수신자가 자신이 되면 안됨!");
					return false;
				}
				
				if( !(currentTransaction.outputs.get(1).reciepient.equals(currentTransaction.sender))) {
					System.out.println("#Block(" + i + ")   Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		
		return true;
	}
	
	
	
	public static void writeBlockchain(ArrayList<Block> blockchain) {
		try {
			   String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
	           FileCtl.writeFile(BLOCKCHAIN_FILE_NAME,  jsonOutput, true);
	  	} catch (Exception e) {
	  		FileLog.writeErrorLog(e.getMessage());
			 e.printStackTrace();
		}
	}
	
	public static void readBlockchain () {
		try{
			
			if (FileCtl.readFile(BLOCKCHAIN_FILE_NAME) == null) return;
			String blockchainJson =  FileCtl.readFile(BLOCKCHAIN_FILE_NAME);
			blockchain = new Gson().fromJson(blockchainJson, new TypeToken<ArrayList<Block>>(){}.getType());
            if (blockchain == null) blockchain = new ArrayList<Block>();
        } catch (Exception e) {
        	FileLog.writeErrorLog(e.getMessage());
        	e.printStackTrace();
        }
	}
	
}