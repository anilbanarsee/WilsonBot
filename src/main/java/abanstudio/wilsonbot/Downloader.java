/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import abanstudio.utils.sqlite.DBHandler;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.handle.obj.IChannel;

/**
 *
 * @author Reetoo
 */
public class Downloader implements Runnable{
    
    private String url, name;
    private IChannel channel;
    private boolean checkLength;
    
    public Downloader(String url, String name, IChannel channel, boolean checkLength){
        this.url = url;
        this.name = name;
        this.channel = channel;
        this.checkLength = checkLength;
    }




    public static void main(String[] args) throws MalformedURLException, InterruptedException{
        try {
            download("https://www.youtube.com/watch?v=gRDx9IaZ5zE","hello",null,true);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static File download(String url, String name, IChannel channel, boolean checkLength) throws IOException, InterruptedException{
        
        
        final int maxMinutes = 20;
        final int warnMinutes = 10;
        final int maxClipSize = 10;
        final int maxBytes = 3145728;
        
        File file = null;
        try {
            file = FileDownloader.downloadFile(url, "assets/downloaded");
            
        } catch (InvalidDownloadException ex) {
            if(ex.getMessage().equals("type"))
            System.out.println("URL given was not a direct http audio/video file, checking if youtube.");
            else
            System.out.println("URL given pointed to a file about the limit (30 MB)");
        }
        if(file!=null){
          return file;
        }
        if(URLParser.checkURL(url)){
            String video = url.substring(url.lastIndexOf("=")+1);
            System.out.println(video);
            long minutes = YTApi.getLength(video);
            if(checkLength){
                if(YTApi.getLengthS(video)>maxClipSize){
                    WilsonServer.sendMessage(channel, "Clip was over the "+maxClipSize+" second limit for clips, use the 3rd and 4th parameters to set a timeframe for the clip");
                    return null;
                }
            }
            if(minutes<maxMinutes){
                
                if(minutes>warnMinutes){
                    WilsonServer.sendMessage(channel, "Warning, file is over 10 minutes, may take some time");
                }
                
                file = YTDownloader.download(url);
                System.out.println("File finished downloading");
                System.out.println(file.getName());
                
            }
            else
                 WilsonServer.sendMessage(channel, "Source file was over the "+maxMinutes+" minute limit");
        }
        else{
             WilsonServer.sendMessage(channel, "Given url was not youtube or a file");
             return null;
        }
        return file;
    }

    @Override
    public void run() {
        try {
            download(url, name, channel, checkLength);
        } catch (IOException ex) {
            WilsonServer.sendMessage(channel, "There was an error with the download, please try again or use a different link.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
