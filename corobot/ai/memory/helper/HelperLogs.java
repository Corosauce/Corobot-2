package corobot.ai.memory.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperLogs {

	public static String logFolderPath = "";

	public static String getLogPath() {
		if (logFolderPath.equals("")) {
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			Date date = new Date();
			
			logFolderPath = "." + File.separator + "logs" + File.separator + dateFormat.format(date);
			
			File fileTest = new File(logFolderPath);
			int index = 1;
			if (fileTest.exists()) {
				String tryPath = "";
				while (fileTest.exists()) {
					tryPath = logFolderPath + "(" + index++ + ")";
					fileTest = new File(tryPath);
				}
				if (!tryPath.equals("")) {
					logFolderPath = tryPath;
				}
			}
			
			File fileCheck = new File(logFolderPath);
			fileCheck.mkdirs();
			//logFolderPath += File.separator + "pingtimes.txt";
		}
		
		return logFolderPath;
	}

	public static void appendLineToFile(String string, String filePath) {
		try
		{
		    FileWriter writer = new FileWriter(filePath, true);
		    
		    writer.append(string);
		    writer.append('\n');
	  
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		}
	}
	
	public static void appendLogLineToFile(String string, String filePath) {
		try
		{
		    FileWriter writer = new FileWriter(filePath, true);
		    

			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date date = new Date();
		    writer.append(dateFormat.format(date) + ": " + string);
		    writer.append('\n');
	  
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		}
	}
	
}
