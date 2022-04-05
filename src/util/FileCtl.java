package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
- 클래스명 : FileCtl
- 설      명 : 파일제어
- 생 성  자 : BizAn 
- 생 성  일 : 2020.06.28
*/

public class FileCtl {
	
	private static String BLOCKCHAIN_DIR = Config.BLOCKCHAIN_DIR;
	
	public FileCtl(){
	}
	public static void writeFile(String fileName, String content, boolean bForceUpdate) {
		try {
			   
			   makeDir(Config.BLOCKCHAIN_DIR);  //디렉토리 생성
			
			   String strFile = BLOCKCHAIN_DIR + fileName;
			   File file = new File(strFile);
			   if ((file.exists()))  {
				   if (!bForceUpdate) return ;
			   }
			   
			   FileWriter fileWriter = new FileWriter(strFile);
			   fileWriter.write(content);
			   fileWriter.close();
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public static String readFile (String fileName) {
		try{
			
			String strFile = BLOCKCHAIN_DIR + fileName;
			
			StringBuffer buff = new StringBuffer();
            //파일 객체 생성
            File file = new File(strFile);
            if (!(file.exists())) return null;
           	
            char[] ch = new char[(int)file.length()];
            BufferedReader br = new BufferedReader (new FileReader(file));
            br.read(ch);
            buff.append(ch);
            br.close();
            return  buff.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return null;
	}
	
	public static void deleteFile(String fileName) {
		String strFile = BLOCKCHAIN_DIR + fileName;
        
		try{
        File deleteFile = new File(strFile);
 
        // 파일이 존재하는지 체크 존재할경우 true, 존재하지않을경우 false
        if(deleteFile.exists()) {
            // 파일을 삭제합니다.
            deleteFile.delete(); 
        }
		} catch (Exception e) {
	       	e.printStackTrace();
	    }
	}
	
	public static void makeDir(String folderPath) {
			try{
			        // 폴더를 만들 디렉토리 경로(Window 기반)
			        //String folderPath = Config.BLOCKCHAIN_DIR;
			        
			        File makeFolder = new File(folderPath);
			        if(!makeFolder.exists())   makeFolder.mkdir(); 
			 } catch (Exception e) {
				   	e.printStackTrace();
			}
	}
}