package util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;

/*
- 클래스명 : RsaEncryptDecrypt
- 생 성  자 : BizAn
- 설      명 :  RSA 암호화/복호화
- 생 성  일 : 2020.06.29
*/

public class RsaEncryptDecrypt {
	
	private static String KEY_FACTORY_ALGORITHM = "RSA";
	private static int MAX_ENCRYPT_BLOCK = 117;     
	private static int MAX_DECRYPT_BLOCK = 128;
	
	
	public RsaEncryptDecrypt () {
	}
		
	public static String rsaEncrypt (String publicKey, String plainText)  {
		
		String result = "";
		
		try {
		     // encryption
            Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, RSAKey.generatePublicKey(Base64.decodeBase64(publicKey.getBytes())));
			byte[] inputArray = plainText.getBytes();
			int inputLength = inputArray.length;
			
			 // logo
			int offSet = 0;
			byte[] resultBytes = {};
			byte[] cache = {};
			while (inputLength - offSet > 0) {
				if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
					offSet += MAX_ENCRYPT_BLOCK;
				} else {
					cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
					offSet = inputLength;
				}
				resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
				System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
			}
			result = Base64.encodeBase64String(resultBytes);
		} catch (Exception e) {
			FileLog.writeErrorLog(e.getMessage());
			//System.out.println("rsaEncrypt error:" + e.getMessage());
		}
		 //System.out.println("Encrypted result: " + result);
		return result;
   }
	
	public static String rsaDecrypt (String privateKey, String cipherText)  {
		try  {
		   Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
           cipher.init(Cipher.DECRYPT_MODE, RSAKey.generatePrivateKey(Base64.decodeBase64(privateKey.getBytes())));
        
            byte[] encryptedData = Base64.decodeBase64(cipherText.getBytes()) ;
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            
           // Decrypt the data in segments
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            
            return new String(decryptedData) ;
            
        } catch (Exception e) {
        	FileLog.writeErrorLog(e.getMessage());
			//System.out.println("rsaEncrypt error:" + e.getMessage());
        }
		
		return "" ;
    }
		
}