package main;

import java.util.ArrayList;
import java.util.Date;

import util.Config;
import util.FileLog;
import util.Hash;
import util.StringUtil;

/*
- 클래스명 : Block 
- 설      명 : 블록
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.02
*/

public class Block {
	public String version = Config.SOFTWARE_VERSION;
	public String hash;                                                                 //현재 블록 해시값
	public String previousHash;                                                  //직전 블록 해시값
	public String merkleRoot;                                                     //트랙잭션 이진트리 해시값
	public long timeStamp;                                                        //1970년 1월 1일부터 시작된 밀리초 (as number of milliseconds since 1/1/1970.)
	public int nonce;                                                                    //채굴성공 시퀀스 값
	public int height;                                                                    //블록체인 위치한 높이(blockchain array address height - 1)
	public int difficulty;                                                                //채굴 난이도
	public String miner;                                                               //채굴자
	public long mineTime;                                                          //1970년 1월 1일부터 시작된 밀리초 (as number of milliseconds since 1/1/1970.)
	public int transactionsCnt ;                                                   //블록 트랜잭션 보유 수
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();     //블록 트랜잭션 목록 
	
	//생성자 
	public Block() {
		if (Blockchain.blockchain.size() == 0) {
			this.previousHash = "0";
		} else {
			this.previousHash = Blockchain.blockchain.get(Blockchain.blockchain.size()-1).hash;
		}
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash(); //다른 값 선행 설정 후 이 값 계산하여야 함
	}
	
	//해시값 계산 (직전 해시값 + 타입스탬프 + 넌스 + 머클루트)
	public String calculateHash() {
		
		String calculatedhash = Hash.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				merkleRoot
				);
		return calculatedhash;
	}
	
	//채굴 (제네시스 블럭 Only)
	public void mineBlock(int difficulty) {
		long startTime;
		long endTime;
		this.difficulty = difficulty;
		
		merkleRoot = getMerkleRoot(transactions);
		String target = this.getDificultyString(difficulty); //Create a string with difficulty * "0" 
		startTime = new Date().getTime();
		
		FileLog.writeInfoLog("채굴중...");
		//System.out.println("블럭 채굴중... ");
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
			//System.out.println("hash : " + hash);
		}
		endTime = new Date().getTime();
		
		this.mineTime = endTime - startTime;
		
		FileLog.writeInfoLog("채굴됨! :" + hash);
		
		//System.out.println("블럭 채굴됨! : " + hash);
	}
	
	//채굴.
		public boolean mineBlock2(int difficulty) {
			long startTime;
			long endTime;
			this.difficulty = difficulty;
			
			
			this.transactions = Mempool.transactions;
			if (this.transactions.size() < Mempool.MAX_MEMPOOL_SIZE) {
				FileLog.writeErrorLog("맴풀 내 트랜잭션 : " + this.transactions.size() + " 개 => 트랜잭션 맴풀 크기가 충분하지 않습니다.");
				//System.out.println("트랜잭션 맴풀 크기가 충분하지 않습니다.");
				return false;
			}
			
			//System.out.println("블럭 채굴종... ");
			FileLog.writeInfoLog("채굴종... ");
			
			merkleRoot = getMerkleRoot(this.transactions);
			String target = this.getDificultyString(difficulty); //Create a string with difficulty * "0" 
			startTime = new Date().getTime();
			
			while(!hash.substring( 0, difficulty).equals(target)) {
				nonce ++;
				hash = calculateHash();
				//System.out.println("hash : " + hash);
			}
			endTime = new Date().getTime();
			
			this.mineTime = endTime - startTime;
			
			FileLog.writeInfoLog("채굴됨! :" + hash);
			//System.out.println("블럭 채굴됨! : " + hash);
			
			return true;
		}
		
	//블록에 트랜잭션 목록 추가
	public boolean addTransaction(Transaction transaction) {
		//process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;	
		
		if(previousHash != "0") {
			if(transaction.processTransaction() != true) {
				FileLog.writeErrorLog("처리중 오류가 발생하여 해당 트랜잭션은 버려졌습니다.");
				//System.out.println("처리중 오류가 발생하여 해당 트랜잭션은 버려졌습니다.");
				return false;
			}
		}		
		transactions.add(transaction);
		
		//FileLog.writeInfoLog("트랜잭션이 블럭에 추가 되었습니다.");
		//System.out.println("트랜잭션이 블럭에 추가 되었습니다.");
		return true;
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(Hash.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		
		return merkleRoot;
	}
	
	private String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
}