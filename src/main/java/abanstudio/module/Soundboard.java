/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.CoreAction;
import abanstudio.discordbot.BotServer;
import abanstudio.utils.sqlite.DBHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;

/**
 *
 * @author Reetoo
 */
public class Soundboard extends Module{
    
    Track currentTrack;
        
    public IVoiceChannel getConnectedChannel(IGuild guild){
        List<IVoiceChannel> channels = client.getConnectedVoiceChannels();
        for(IVoiceChannel channel : channels){
            if(channel.getGuild().getID().equals(guild.getID())){
                return channel;
            }
        }
        System.out.println("CRITICAL ERROR");
        return null;
    }
    
    @EventSubscriber
    public void trackChange(TrackStartEvent event){
        Track t = event.getTrack();
        currentTrack = t;
        String trackName = t.getMetadata().get("name").toString();
        
        IUser banner = checkTrackForChannel(getConnectedChannel(event.getPlayer().getGuild()),t);
        
        if(banner!=null){
            IChannel channel = (IChannel) t.getMetadata().get("textchannel");
            server.sendMessage(channel, "Clip :"+trackName+" was skipped as "+banner.getName()+" has voted to ban the clip and is present within this channel.");
            currentTrack = null;
            event.getPlayer().skip();
            return;
        }
    
        
        currentTrack = t;
        t.getMetadata().put("played", true);
        AudioPlayer player = event.getPlayer();
        player.setVolume((float) t.getMetadata().get("volume"));
    }
    public IUser checkTrackForChannel(IVoiceChannel voicechannel, Track t){
        
        List<IUser> users = voicechannel.getConnectedUsers();
        if(currentTrack!=null){
            String clipName = t.getMetadata().get("name").toString();
            ArrayList<String> banners = DBHandler.getBanners(clipName);
            for(IUser u: users){
            
                for(String id: banners){
                    System.out.println(id);
                    System.out.println(u.getID());
                    System.out.println(id.equals(u.getID()));
                    if(id.equals(u.getID())){
                        
                        return u;
                    }
                }
            
            }
        }
        
        return null;
        
    }
    @EventSubscriber
    public void trackEnd(TrackFinishEvent event){
        
        currentTrack = null;
    }
    @Override
    protected void initalizeActions() {
        actionMap = new HashMap<>();
        
        actionMap.put("ping",  new Action(){public void exec(String[] arg, IMessage m) {ping(arg,m);}});   
        overrides.put("join", new Action(){
            @Override
            public void exec(String[] arguments, IMessage message) {
                join(arguments, message);
            }
        });
        
    }

    @Override
    protected void initalizeCommData() {
        String[][] comms = {{"[jJ]oin", "join", "joins a channel, will skip any clips currently banned by users within the channel"}};
        commData = comms;
    }
    
    public void ping(String[] arg, IMessage m){
        server.sendMessage(m.getChannel(), "pong");
    }

    @Override
    public String getName() {
        return "Soundboard";
    }
    
    public void onReady(){
        
    }
    
    public void join(String[] args, IMessage m){
        
    }

}
