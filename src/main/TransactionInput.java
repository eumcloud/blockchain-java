package main;

import main.TransactionOutput;

/*
- 클래스명 : TransactionInput
- 설      명 : 트랜잭션 입력값
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.01
*/

public class TransactionInput {
	public String transactionOutputId;	     //TransactionOutputs -> transactionId 참조
	public TransactionOutput UTXO; 	 	 //UTXO 포함
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}