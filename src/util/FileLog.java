package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
- 클래스명 : FileLog
- 설      명 : 로그파일 생성 
- 생 성  자 : BizAn
- 생 성  일 : 2020.09.03
*/


public class FileLog {
	
	public static String curPath = Config.BLOCKCHAIN_DIR + "\\Log\\";
	
	public FileLog () {
		  //Path relativePath = Paths.get("");
	      //curPath = relativePath.toAbsolutePath().toString();
		  //FileCtl.makeDir(curPath);
	}
	
    public static synchronized void writeDebugLog(String sLog)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
        String sToday = formatter.format(cal.getTime()); //for log-time
        String sDate = formatter2.format(cal.getTime()); //for file-name

        //Path relativePath = Paths.get("");
	    //curPath = relativePath.toAbsolutePath().toString() + "\\Log\\";
        //System.out.println(curPath);
        
        String sFileName = curPath + sDate +"_Debug.Log";
        sLog = "[" + sToday + "]:" + sLog;

        try
        {
        	FileCtl.makeDir(curPath);
        	
            BufferedWriter bw = new BufferedWriter(new FileWriter(sFileName, true));
            bw.write(sLog);
            bw.newLine();
            bw.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
    
    public static synchronized void writeInfoLog(String sLog)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
        String sToday = formatter.format(cal.getTime()); //for log-time
        String sDate = formatter2.format(cal.getTime()); //for file-name

        //Path relativePath = Paths.get("");
	    //curPath = relativePath.toAbsolutePath().toString() + "\\Log\\";
        //System.out.println(curPath);
        
        String sFileName = curPath + sDate +"_Info.Log";
        sLog = "[" + sToday + "]:" + sLog;

        try
        {
        	FileCtl.makeDir(curPath);
        	
            BufferedWriter bw = new BufferedWriter(new FileWriter(sFileName, true));
            bw.write(sLog);
            bw.newLine();
            bw.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
    
    public static synchronized void writeErrorLog(String sLog)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
        String sToday = formatter.format(cal.getTime()); //for log-time
        String sDate = formatter2.format(cal.getTime()); //for file-name

        //Path relativePath = Paths.get("");
	    //curPath = relativePath.toAbsolutePath().toString() + "\\Log\\";
        //System.out.println(curPath);
        
        String sFileName = curPath + sDate +"_Error.Log";
        sLog = "[" + sToday + "]:" + sLog;

        try
        {
        	FileCtl.makeDir(curPath);
        	
            BufferedWriter bw = new BufferedWriter(new FileWriter(sFileName, true));
            bw.write(sLog);
            bw.newLine();
            bw.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
    
}