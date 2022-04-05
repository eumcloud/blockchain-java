package main;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import util.Config;
import util.FileCtl;
import util.FileLog;

/*
- 클래스명 : Mempool
- 설      명 : 맴풀 생성
- 생 성  자 : BizAn 
- 생 성  일 : 2020.07.05
*/

public class Mempool {
	
	public static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public static int MAX_MEMPOOL_SIZE = Config.MAX_MEMPOOL_SIZE;
	private static String MEMPOOL_FILE_NAME = Config.MEMPOOL_FILE_NAME;
	
	public Mempool(){
		//mempool read
		 //readMempool();
	}
	
	public static void writeMempool(ArrayList<Transaction> transactions) {
		try {
			   String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson(transactions);   //트랜잭션 배열을 JSON 형식의 스트링값으로 변환
	           FileCtl.writeFile(MEMPOOL_FILE_NAME, jsonOutput, true);                                                //맴풀 파일에 JSON값 추가
	  	} catch (Exception e) {
	  		FileLog.writeErrorLog(e.getMessage());
			 e.printStackTrace();
		}
	}
	
	public static void readMempool () {
		try{
			
			String transactionJson =  FileCtl.readFile(MEMPOOL_FILE_NAME);
			
			//맴풀 파일의 JSON 형식의 스트링 값을 읽어서, 트랜잭션 배열형식으로 변환
             transactions = new Gson().fromJson(transactionJson, new TypeToken<ArrayList<Transaction>>(){}.getType());
             if (transactions == null) transactions = new ArrayList<Transaction>();    //트랜잭션 배열이 NULL이면 배열 초기화
        } catch (Exception e) {
        	FileLog.writeErrorLog(e.getMessage());
        	e.printStackTrace();
        }
	}
	
	//맴풀파일 초기화(삭제)
	public static void resetMempool() {
		try {
			   FileCtl.deleteFile(MEMPOOL_FILE_NAME);
	  	} catch (Exception e) {
	  		FileLog.writeErrorLog(e.getMessage());
			 e.printStackTrace();
		}
	}
}