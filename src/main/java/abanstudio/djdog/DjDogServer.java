/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.djdog;

import abanstudio.utils.sqlite.DBHandler;
import abanstudio.wilsonbot.Downloader;
import abanstudio.wilsonbot.FFMPEG;
import abanstudio.wilsonbot.YTDownloader;
import java.io.File;
import java.io.IOException;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import abanstudio.wilsonbot.Main;
import sx.blah.discord.api.IDiscordClient;

/**
 *
 * @author User
 */
public class DjDogServer {
    
    boolean loop;
    IDiscordClient client;
    
    public DjDogServer(IDiscordClient client){
        
        this.client = client; 
        
    }
    
    @EventSubscriber
    public void onReady(ReadyEvent event){
        System.out.println("DjDog Ready");
    }
    @EventSubscriber
    public void onDisconnect(DiscordDisconnectedEvent event) throws DiscordException{
        System.out.println("DjDog disconnected");
    }
    
    public void download(String[] arguments, IMessage message) throws IOException, InterruptedException{
        
        String name = arguments[1];
        
        
        File f = Downloader.download(arguments[0],name,message.getChannel(),false);
        
        
        
        
        Main.ffmpeg.convertAndTrim(f, name, null, "music/");
       
        int clip  = DBHandler.addClip(name,0,0,arguments[0],message.getAuthor().getID());
        
        DBHandler.addClipToUser(clip, message.getAuthor().getID());
        
    }
    public void play(String[] arguments, IMessage message) throws DiscordException{
        
        File f = new File("assets/music/"+arguments[0]);
        
        client.getGuildByID(message.getGuild().getID()).getAudioChannel().queueFile(f);
        
    }
    public void clear(String guildID) throws DiscordException{
        client.getGuildByID(guildID).getAudioChannel().clearQueue();
    }
    
}
