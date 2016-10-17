/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Reetoo
 */
public class ReadingThread extends Thread{
    
    public String filename;
    private Process p;
    
    
    public ReadingThread(Process p){
        super();
        this.p = p;
    }
    
    @Override
     public void run() {
         
     
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null; 
            System.out.println("HELLLLLLLO");

            try {
                while ((line = input.readLine()) != null){
                    if(line.startsWith("[youtube]")){
                        int i = line.lastIndexOf("]");
                        String s = line.substring(i+1,line.length()-1);
                        String[] a = s.split(":");
                        String file = a[0];
                        filename = file.replaceAll("\\s", "");
                    }
                    else if(line.startsWith("[ffmpeg]")){
                        int i = line.lastIndexOf("\\");
                        String[] a =  {line.substring(0, i), line.substring(i)};
                        filename = a[1].substring(1, a[1].length()-1);
                    }
                    
                    System.out.println(line);
                    
                }
            } catch (IOException e) {
                    e.printStackTrace();
            }
     }
    
}
