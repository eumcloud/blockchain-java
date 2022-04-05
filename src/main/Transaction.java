package main;

import java.util.ArrayList;

import util.Config;
import util.FileLog;
import util.Hash;
import util.SigGenVerify;
import util.StringUtil;

/*
- 클래스명 : Transaction
- 설      명 : 트랜잭션 생성
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.04
*/

public class Transaction {
	
	private static float MINIMUN_TRANSACTION = Config.MINIMUN_TRANSACTION;
	
	public String transactionId;                                        // 트랜잭션 ID (해시값).
	public String sender;                                                  // 송신자 공개키
	public String reciepient;                                             // 수신자 공개키
	public float value;                                                      //거래 금액
	public String signature;                                              //거래 전자 서명 (다른 사람이 전자지갑 자금 사용을 방지하기 위함)
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();                  //트랜잭션 입력 값 배열
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();          //트랜잭션 출력 값 배열
	
	private static int sequence = 0;                              // 생성된 대략적인 트랜잭션 수
	
	
	// 생성자
	public Transaction(String from, String to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		if (inputs != null) this.inputs = inputs;
		if (inputs == null) this.transactionId = "0";
	}
	
	// 트랜잭션 ID 해시값 계산
	private String calulateHash() {
		sequence++;                               //동일한 해시를 갖는 2 개의 동일한 트랜잭션을 피하기 위해 시퀀스 증가
		return Hash.applySha256(
				sender +
				reciepient +
				Float.toString(value) + sequence
				);
	}
	
	//트랜잭션에 대한 전자서명 생성(트랜잭션 값 변조방지)
	public void generateSignature(String privateKey) {
		String data = Hash.applySha256(sender + reciepient + Float.toString(value));
		signature =  SigGenVerify.generateSignature(privateKey, data);	                           //송신자 개인키로 전자서명
	}
		
	//트랜잭션 전자서명 검증(트랜잭션 값 변조 확인)
	public boolean verifiySignature() {
		String data = Hash.applySha256(sender + reciepient  + Float.toString(value));
		return SigGenVerify.verifySignature(sender, data, signature);                                //송신자 공개키로 전자서명 검증
	}
	
	//트랜잭션 처리(생성)	
	public boolean processTransaction() {
			
			if(verifiySignature() == false) {
				FileLog.writeErrorLog("트랜잭션 전자서명 검증에 실패하였습니다." );
				//System.out.println("#Transaction Signature failed to verify");
				return false;
			}
					
			//트랜잭션 입력 수집 (입력값이 출력값으로 사용되지 않았는지 확인)
			for(TransactionInput i : inputs) {
				i.UTXO = TransactionUtxo.UTXOs.get(i.transactionOutputId);
			}

			//트랜잭션이 유효한지 확인
			if(getInputsValue() < MINIMUN_TRANSACTION) {
				FileLog.writeErrorLog("트랜잭션 적은 입력값 입력 : "  + getInputsValue());
				//System.out.println("#Transaction Inputs to small: " + getInputsValue());
				return false;
			}
			
			//트랜잭션 출력값 생성
			float leftOver = getInputsValue() - value;             //입력 값을 얻은 다음 남은값 가져오기 (get value of inputs then the left over change:)
			transactionId = calulateHash();
			outputs.add(new TransactionOutput( this.reciepient, value, transactionId));          //수신자에게 트랜잭션 금액 출력값으로 전송
			outputs.add(new TransactionOutput( this.sender, leftOver, transactionId));          //트랜잭션 후 남은 잔액 송신자에게 출력값으로 전송		
					
			//사용되지 않은 목록에 출력 추가
			for(TransactionOutput o : outputs) {
				TransactionUtxo.UTXOs.put(o.id , o);
			}
			
			//지출된 UTXO 목록에서 트랜잭션 입력값 제거
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue;                              //거래를 찾을 수 없으면 건너 뜀 
				TransactionUtxo.UTXOs.remove(i.UTXO.id);
			}
			
			return true;
		}
		
	    //inputs(UTXOs) 값 합계 계산
	   public float getInputsValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				total += i.UTXO.value;
			}
			return total;
	   }

	    //출력값 합계 계산:
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.value;
			}
			return total;
		}
}