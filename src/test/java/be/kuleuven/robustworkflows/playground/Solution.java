package be.kuleuven.robustworkflows.playground;
import java.io.*;

import javax.management.RuntimeErrorException;
public class Solution {
    public static void main(String args[] ) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	Integer seriesSize = Integer.parseInt(br.readLine()); 
       
        int previous = 0;
        int current = 0;
        int dif = 0;
        
        Integer[] series = getInteger(); 
        
        previous = series[0];
        current = series[1];
        dif = current - previous;
        previous = current;
        
        for  (int i=2; i<series.length; i++) {
            current = series[i]; 
            if (dif != (current - previous)) {
                System.out.println(previous + dif);
                return;
            } else {
                previous = current;
             
            }         
        }
    }
    
    public static Integer[] getInteger() {
    	String line;
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	try {
    		line = br.readLine();
    		String[] chuncks = line.trim().split(" +");
    		Integer[] ret = new Integer[chuncks.length];
    		for (int i=0; i<chuncks.length; i++) {
    			ret[i] = Integer.parseInt(chuncks[i]);
    		}
    		
    		return ret;
    	} catch (Exception e) {
    		throw new RuntimeException(e.toString());
    	}
    }
}