package mpfk.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

public class LoadSettings {
	private final static String SETTINGSFILEPATH = "./settings/settings.txt";
	private final String FILESEPARATOR = File.separator;
	private static String lines[];
	
	public LoadSettings() {
		loadSettings();
	}
	
	public void loadSettings() {
		try {
			File textFile = new File(SETTINGSFILEPATH);		
			FileInputStream fis = new FileInputStream(textFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			String outString = IOUtils.toString(isr);
			lines = outString.split("\\r?\\n");
			
			fis.close();
		} catch (FileNotFoundException e) {
			File yourFile = new File(System.getProperty("user.dir") + SETTINGSFILEPATH.substring(1, SETTINGSFILEPATH.length()));
			try {
				yourFile.getParentFile().mkdirs();
				yourFile.createNewFile();

				BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SETTINGSFILEPATH), StandardCharsets.UTF_8));
				ArrayList<String> settingsArray = new ArrayList<String>();
				
				settingsArray.add("mainDir");
				settingsArray.add(System.getProperty("user.dir") + "");
				settingsArray.add("");
				settingsArray.add("movieDir");
				settingsArray.add("E:" + FILESEPARATOR + "Downloads 2019");
				settingsArray.add("");
				settingsArray.add("endOfSettings");
				
				File theDir = new File(System.getProperty("user.dir") + "\\temp");

				// if the directory does not exist, create it
				if (!theDir.exists()) {
					try{
				        theDir.mkdir();
				    } catch(SecurityException se){
				        //handle it
				    	se.printStackTrace();
				    }        
				}
				
				for (String sA : settingsArray) {
					outputWriter.write(sA);
					outputWriter.newLine();
				}
				
				outputWriter.flush();
				outputWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public String getSettings(String line) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].toLowerCase().contains(line.toLowerCase())) {
				return lines[i+1];
			}
		}
		
		return null;
	}

	public static String changeSettings(String newContent, String whichContent) {
		try {
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].toLowerCase().contains(whichContent.toLowerCase())) {
					lines[i+1] = newContent;
				}
			}
			
			FileWriter fileWritter = new FileWriter(SETTINGSFILEPATH);
			BufferedWriter outputWriter = new BufferedWriter(fileWritter);
			
			for (int i = 0; i < lines.length; i++) {
				outputWriter.write(lines[i]);
				outputWriter.newLine();
			}
			
			outputWriter.flush();
			outputWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}

		return newContent;
	}
}