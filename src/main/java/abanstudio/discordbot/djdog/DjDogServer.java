/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot.djdog;

import abanstudio.command.Action;
import abanstudio.discordbot.BotServer;
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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;

/**
 *
 * @author User
 */
public class DjDogServer extends BotServer {
    
    boolean loop;
    IDiscordClient client;
    
    public DjDogServer(IDiscordClient client){
        
        super(client);
        prefix = "dj";
        
    }
    
    @EventSubscriber
    @Override
    public void onReady(ReadyEvent event){
        System.out.println("DjDog Ready");
    }
    @EventSubscriber
    @Override
    public void onDisconnect(DiscordDisconnectedEvent event) throws DiscordException{
        System.out.println("DjDog disconnected");
    }
    
    public void save(String[] arguments, IMessage message){
        
        
        sendMessage(message.getChannel(),"Sure thing boss");
        
        String name = arguments[1];
        
        
        File f = null;
        try {
            f = Downloader.download(arguments[0],name,message.getChannel(),false);
        } catch (IOException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        String[] time = null;
        
        try {
            Main.ffmpeg.convertAndTrim(f, name, time, "music/");
        } catch (IOException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        int clip  = DBHandler.addClip(name,0,0,arguments[0],message.getAuthor().getID());
        
        DBHandler.addClipToUser(clip, message.getAuthor().getID());
        
        sendMessage(message.getChannel(),"Music added to my collection");
        
    }
    public static void download(IGuild guild, IMessage message, String commands){
        
        String m = message.getContent().substring(5,6);
        if(m.equals("f")){
            System.out.println("Stack size = 5");
        }
    
        
    }
    public void skip(IGuild guild) throws DiscordException{
         AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
         player.skip();
    }
    public void play(String[] arguments, IMessage message){
        
        File f = new File("assets/music/"+arguments[0]+".mp3");
        
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(message.getGuild());
        try {
            player.queue(f);
        } catch (IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(DjDogServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void clear(IMessage message){
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(message.getGuild());
        player.skip(); 
    }

    @Override
    public boolean isAdmin(IUser user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void initalizeActions() {
        
        actionMap = new HashMap<>();
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {join(arg,m);}});
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {save(arg,m);}});
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {clear(m);}});
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {play(arg,m);}});
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {setVolume(arg,m);}});




        
        
    }
    public void setLoop(IMessage message){
        IGuild guild = message.getGuild();
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        Track track = player.getCurrentTrack();
        player.clean();
        player.setLoop(true);
    }
    public void setVolume(String[] args, IMessage message){
        
    }
    
    @Override
    protected void initalizeCommData() {
        String[][] comms = null;
        commData = comms;
    }
    
}
