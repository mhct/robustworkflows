package be.kuleuven.robustworkflows.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.Files;

public class ConvertCSV {
	public static void main(String[] args) throws IOException {
		BufferedReader originalFile = Files.newReader(new File(args[0]), Charset.defaultCharset());
		BufferedWriter newFile = Files.newWriter(new File("new_" + args[0]), Charset.defaultCharset());
		
		String line = null;
		String newLine = null;
		StringBuilder sb = new StringBuilder();
		while ((line = originalFile.readLine()) != null) {
			String[] chunks = line.split(",");
			
			if (chunks.length == 5) {
				newLine = '"' + chunks[0] 
						+ "\"," + chunks[1]
						+ "," + chunks[2]
						+ "," + chunks[3]
						+ ",\"" + chunks[4]
						+ "\"\n";
				sb.append(newLine);
			} else {
				throw new RuntimeException("Data in wrong format: " + line);
			}
		}
		
		newFile.write(sb.toString());
		
	}
}
