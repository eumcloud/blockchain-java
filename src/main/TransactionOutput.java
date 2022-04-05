package main;

import util.Hash;

/*
- 클래스명 : TransactionOutput
- 설      명 : 트랜잭션 출력값
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.013
*/

public class TransactionOutput {
	public String id;
	public String reciepient;                      //트랜잭션 코인의 소유주
	public float value;                               //소유한 코인의 양
	public String parentTransactionId;    //이 출력이 생성된 트랜잭션 ID
	
	//생성자
	public TransactionOutput(String reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = Hash.applySha256(reciepient+Float.toString(value)+parentTransactionId);
	}
	
	//코인이 당신 소유인지 확인
	public boolean isMine(String publicKey) {
		return (publicKey == reciepient);
	}
	
}