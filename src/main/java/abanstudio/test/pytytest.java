/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.wilsonbot.ReadingThread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Reetoo
 */
public class pytytest {
    
    public static void main(String[] args) throws IOException, InterruptedException{
        final String filename;
        final Process p = Runtime.getRuntime().exec("youtube-dl.exe -o ~/Desktop/yttest/%(title)s.%(ext)s https://www.youtube.com/watch?v=ANB5zTl-nrY");
       
        ReadingThread rt = new ReadingThread(p);
        
        rt.start();

        p.waitFor();
        System.out.println(rt.filename);
        //return new File("downloaded/"+filename);
    }
}
