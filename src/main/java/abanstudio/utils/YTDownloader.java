/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.utils;

import com.github.axet.vget.VGet;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author Reetoo
 */
public class YTDownloader {
    

        public static File download(String url) throws IOException, InterruptedException{
            String filename;
            final Process p = Runtime.getRuntime().exec("youtube-dl.exe -o C:/Users/Reetoo/Documents/NetBeansProjects/WilsonBot/assets/downloaded/%(id)s.%(ext)s "+url );
            
            ReadingThread rt = new ReadingThread(p);
            rt.start();
            p.waitFor();
            System.out.println("HellO"+"assets/downloaded/"+rt.filename);
            return new File("assets/downloaded/"+rt.filename);
        }

}
