/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.utils;

import abanstudio.discordbot.BotServer;
import abanstudio.utils.FileDownloader;
import abanstudio.utils.URLParser;
import abanstudio.exceptions.InvalidDownloadException;
import abanstudio.utils.YTApi;
import abanstudio.utils.YTDownloader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.handle.obj.IChannel;

/**
 *
 * @author Reetoo
 */
public class Downloader implements Runnable
{
    
    private String url, name;
    private IChannel channel;
    private boolean checkLength;
    private BotServer server;
    
    public Downloader(String url, String name, IChannel channel, boolean checkLength, BotServer server){
        this.url = url;
        this.name = name;
        this.channel = channel;
        this.checkLength = checkLength;
        this.server = server;
        
    }




    public static void main(String[] args) throws MalformedURLException, InterruptedException{
        try {
            download("https://www.youtube.com/watch?v=gRDx9IaZ5zE","hello",null,true);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static File download(String url, String name, IChannel channel, boolean checkLength, BotServer server) throws IOException, InterruptedException{
        
        
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

            if(minutes<maxMinutes){
                
                if(minutes>warnMinutes){
                    server.sendMessage(channel, "Warning, file is over 10 minutes, may take some time");
                }
                
                file = YTDownloader.download(url);
                System.out.println("File finished downloading");
                System.out.println(file.getName());
                
            }
            else
                 server.sendMessage(channel, "Source file was over the "+maxMinutes+" minute limit");
        }
        else{
             server.sendMessage(channel, "Given url was not youtube or a file");
             return null;
        }
        return file;
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
            /*if(checkLength){
                if(YTApi.getLengthS(video)>maxClipSize){
                    System.out.println("Clip was over the "+maxClipSize+" second limit for clips, use the 3rd and 4th parameters to set a timeframe for the clip");
                    return null;
                }
            }*/
            if(minutes<maxMinutes){
                
                if(minutes>warnMinutes){
                    System.out.println("Warning, file is over 10 minutes, may take some time");
                }
                
                file = YTDownloader.download(url);
                System.out.println("File finished downloading");
                System.out.println(file.getName());
                
            }
            else
                 System.out.println("Source file was over the "+maxMinutes+" minute limit");
        }
        else{
             System.out.println("Given url was not youtube or a file");
             return null;
        }
        return file;
    }

    
    @Override
    public void run() {
        try {
            download(url, name, channel, checkLength);
        } catch (IOException ex) {
            server.sendMessage(channel, "There was an error with the download, please try again or use a different link.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
