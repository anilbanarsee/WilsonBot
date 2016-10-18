/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot.djdog;

import abanstudio.utils.sqlite.DBHandler;
import abanstudio.utils.Downloader;
import java.io.File;
import java.io.IOException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import abanstudio.discordbot.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;

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
        
        
        sendMessage(message.getChannel(),"Sure thing boss");
        
        String name = arguments[1];
        
        
        File f = Downloader.download(arguments[0],name,message.getChannel(),false);
        
        
        
        
        Main.ffmpeg.convertAndTrim(f, name, null, "music/");
       
        int clip  = DBHandler.addClip(name,0,0,arguments[0],message.getAuthor().getID());
        
        DBHandler.addClipToUser(clip, message.getAuthor().getID());
        
        sendMessage(message.getChannel(),"Music added to my collection");
        
    }
    public void skip(IGuild guild) throws DiscordException{
         AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
         player.skip();
    }
    public void play(String[] arguments, IMessage message) throws DiscordException{
        clear(message.getGuild());
        
        File f = new File("assets/music/"+arguments[0]+".mp3");
        
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(message.getGuild());
        try {
            player.queue(f);
        } catch (IOException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void clear(IGuild guild) throws DiscordException{
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
         player.skip();
    }
    
     public void sendMessage(IChannel channel, String message){

        RequestBuffer.request(() -> {
		try {
			new MessageBuilder(client).withChannel(channel).withContent(message).build();
		} catch (DiscordException | MissingPermissionsException e) {
			e.printStackTrace();
		}
		return null;
	});

    }
    
}
