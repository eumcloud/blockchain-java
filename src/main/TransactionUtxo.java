package main;

import java.util.HashMap;

import com.google.gson.GsonBuilder;

/*
- 클래스명 : TransactionUtxo
- 설      명 : 소비되지 않은 트랜잭션 결과물
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.013
*/

public class TransactionUtxo {
	
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public static void addUtxo(String id, TransactionOutput transactonOutput) {	
		//UTXO 리스트에  트랜잭션 출력 등록
		//UTXOs.clear();
		UTXOs.put(id, transactonOutput); //its important to store our first transaction in the UTXOs list.
		//String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson(transactonOutput);
	}
	
	public static void removeUtxo(String id) {	
		UTXOs.remove(id); 
	}
}