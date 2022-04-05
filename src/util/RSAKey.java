package util;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

import com.google.gson.GsonBuilder;


/*
- 클래스명 : RSAKey
- 설      명 :  RSA 암호화/복호화
- 생 성  자 : BizAn
- 생 성  일 : 2020.06.29
*/

public class RSAKey {
	
	public String privateKey;
	public String publicKey;
	
	private static String KEY_FACTORY_ALGORITHM = "RSA"; 
	private static int RSA_KEY_SIZE =1024;
	
	public RSAKey(){
		generateKeyPair();	
	}
		
	public void generateKeyPair() {
		try {
			    KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
	            generator.initialize(RSA_KEY_SIZE, new SecureRandom());
	            KeyPair keyPair = generator.generateKeyPair();
	            
	            //String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson( keyPair.getPublic());
	    		//System.out.println(jsonOutput);
	            
	    		//  jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson( keyPair.getPrivate());
		    	//System.out.println(jsonOutput);
		    		
	            
	            privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
	            publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
	   }catch(Exception e) {
		        FileLog.writeErrorLog(e.getMessage());
				throw new RuntimeException(e);
		}
	}
	
	public static PublicKey generatePublicKey(byte[] publicKey) {
		try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
        } catch (NoSuchAlgorithmException e) {
        	FileLog.writeErrorLog(e.getMessage());
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
        	FileLog.writeErrorLog(e.getMessage());
            throw new IllegalArgumentException(e);
        }
	}
	
	public static PrivateKey generatePrivateKey(byte[] privateKey) {
	    try {
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
	        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
	    } catch (NoSuchAlgorithmException e) {
	    	FileLog.writeErrorLog(e.getMessage());
	    	throw new RuntimeException(e);
	    } catch (InvalidKeySpecException e) {
	    	FileLog.writeErrorLog(e.getMessage());
	        throw new IllegalArgumentException(e);
	    }
	}
}