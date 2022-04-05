package util;

/*
- 클래스명 : Config
- 설      명 : 블럭체인에 사용할 글로별 상수 정의
- 생 성  자 : BizAn 
- 생 성  일 : 2020.06.30
*/

public class Config {
	
	public static String SOFTWARE_VERSION = "1.0";                                           //블록체에 S/W 버전
	public static int DIFFICULTY = 5;                                                                          //블록 채굴 난이도
	public static float MINIMUN_TRANSACTION = 0.1f;                                          //최소 트랜잭션 값
	public static int MAX_MEMPOOL_SIZE = 5;                                                       //맴풀 최대 크기 지정                                               
	public static String MEMPOOL_FILE_NAME = "mempool.txt";                          //맴풀 파일명 지정
	public static String BLOCKCHAIN_FILE_NAME = "blockchain.txt";                   //블록체인 파일명 지정
	public static int MAX_BLOCKCHIN_SIZE = 1000000;                                       //블록체인 최대 파일 크기 지정.
	public static String BLOCKCHAIN_DIR = "D:\\blockchain4\\";                             //블록체인 생성 디렉토리 지정
	public static String PRIAVTE_KEY_FILE_EXTENTION = ".privateKey.txt" ;     //전자지감 개인키 파일명 지정
	public static String PUBLIC_KEY_FILE_EXTENTION = ".publickey.txt" ;         //전자지감 공개키 파일명 지정
	
}
