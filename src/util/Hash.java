package util;

import java.security.MessageDigest;

/*
- 클래스명 : Hash
- 설      명 : 해시값 생성 
- 생 성  자 : BizAn
- 생 성  일 : 2020.06.21
*/

public class Hash {
	
	public Hash() {
	}
	
	//해시값 리턴
	public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}	
			
}