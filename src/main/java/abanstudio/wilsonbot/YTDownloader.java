/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author Reetoo
 */
public class YTDownloader {
    
        public static void main(String[] args) {
        try {
            // ex: http://www.youtube.com/watch?v=Nj6PFaDmp6c
            String url = "https://www.youtube.com/watch?v=zoMiYklHvjk";
            // ex: "/Users/axet/Downloads"
            String path = "assets/downloaded";
            VGet v = new VGet(new URL(url), new File(path));
                  v.download();
            System.out.println(v.getTarget().getCanonicalPath());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
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
