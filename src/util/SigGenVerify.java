package util;

import java.security.Signature;
import org.apache.commons.codec.binary.Base64;

/*
- 클래스명 : SigGenVerify
- 설      명 : 전자서명 생성과 검증 
- 생 성  자 : BizAn
- 생 성  일 : 2020.06.24
*/

public class SigGenVerify {
	
	private static String SIGNATURE_ALGORITHM = "SHA256withRSA";
	private static String CHARSET = "UTF-8";
	private static int SIGNATURE_BLOCK_LENGTH = 172;
	
	public SigGenVerify() {
	}
	
	//전자 서명 생성
	public static String generateSignature(String privateKey, String plainText) {
	    try {
	        Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        privateSignature.initSign(RSAKey.generatePrivateKey(Base64.decodeBase64(privateKey.getBytes())));
	        privateSignature.update(plainText.getBytes(CHARSET));
	        byte[] signature = privateSignature.sign();
	        return Base64.encodeBase64String(signature);
	    } catch (Exception  e) {
	    	FileLog.writeErrorLog(e.getMessage());
	        throw new RuntimeException(e);
	    }
	}
	
	//전자 설명 검증
	public static boolean verifySignature(String publicKey, String plainText, String sigData) {
	    try {
	    	   
	    	if (sigData.length() != SIGNATURE_BLOCK_LENGTH) {
	    		return false;
	    	}
	    	
    	   Signature  sig = Signature.getInstance(SIGNATURE_ALGORITHM);
	        sig.initVerify(RSAKey.generatePublicKey(Base64.decodeBase64(publicKey.getBytes())));
	        sig.update(plainText.getBytes());
	        if (!sig.verify(Base64.decodeBase64(sigData))) {
	        	return false;
	        }
	        return true;
	    } catch (Exception  e) {
	    	FileLog.writeErrorLog(e.getMessage());
	        throw new RuntimeException(e);
	    }
	}
	
}