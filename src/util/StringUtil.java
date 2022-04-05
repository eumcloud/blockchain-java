package util;

import java.security.Key;
import org.apache.commons.codec.binary.Base64;


/*
- 클래스명 : StringUtil
- 설      명 : 스트링값으로 리턴
- 생 성  자 : BizAn 
- 생 성  일 : 2020.06.20
*/

public class StringUtil {
	
	public StringUtil() {
	}
			
	public static String getStringFromKey(Key key) {
		return Base64.encodeBase64String(key.getEncoded());
	}
	
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}		
}