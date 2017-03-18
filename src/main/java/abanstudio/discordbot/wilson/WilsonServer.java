/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot.wilson;

import abanstudio.discordbot.BotServer;
import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.discordbot.djdog.DjDogServer;
import abanstudio.exceptions.R9KException;
import abanstudio.utils.sqlite.DBHandler;
import abanstudio.utils.Downloader;
import abanstudio.games.Game;
import abanstudio.discordbot.Main;
import abanstudio.games.PokemonGuessGame;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.joda.time.DateTime;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.TrackStartEvent;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.providers.FileProvider;


/**
 *
 * @author Reetoo
 */
public class WilsonServer extends BotServer{
    

    
    

    //ArrayList<ArrayList<Thread>> gameThread;


    
    ArrayList<IUser> parlayUsers;
    
  
  
    HashMap<String, GuildSettings> guildSettings;
    
   
    
    
    
    DjDogServer djdog;
    
    
    

    public WilsonServer(IDiscordClient client, DjDogServer server){
        
        super(client);
        prefix = "dog";
        
        parlayUsers = new ArrayList<>();
        djdog = server;
        
        
        actionMap = new HashMap<>();
        
        //initGuildSettings();
        
    }
    
    
    
    /*
    @Override
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        
        IMessage m = event.getMessage();
        
        
        if(event.getMessage().getAuthor().isBot())
            return;
        String message = event.getMessage().getContent();
        
        if(message.startsWith(prefix+" ")){
            
           
            String command = message;
            IMessage mess = event.getMessage();
            IChannel redirect = commChanMap.get(m.getGuild().getID());
            if(redirect!=null){
                if(!(m.getChannel().equals(redirect))){
                    redirect(m,redirect);
                    
                }
            }
            parseCommand(command, event.getMessage());
           
        }
        
    }
    */
    

    @Override
    public void onServerReady(ReadyEvent event){
        System.out.println("WilsonBot initalizing");
        this.initGuildSettings();
        
    }
    

    
    public void initGuildSettings(){
        guildSettings = new HashMap<>();
        List<IGuild> guilds = client.getGuilds();
        guilds.stream().forEach((g) -> {
            ArrayList<String> data = DBHandler.getGuildInfo(g.getID());
            if(data!=null){
                GuildSettings gs = new GuildSettings(data);
                guildSettings.put(g.getID(), gs);
            }
        });
    }
    
   

    @Override
    protected void initalizeActions(){
         actionMap = new HashMap<>();
        
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {join(arg,m);}});
        actionMap.put("parlay",new Action(){public void exec(String[] arg, IMessage m) {parlay(m);}});
        actionMap.put("unparlay",new Action(){public void exec(String[] arg, IMessage m) {unparlay(m);}});
        actionMap.put("list",new Action(){public void exec(String[] arg, IMessage m) {list(arg,m);}});
     // actionMap.put("set",new Action(){public void exec(String[] arg, IMessage m) {set(arg,m);}});
        actionMap.put("shutdown",new Action(){public void exec(String[] arg, IMessage m) {shutdown(m);}});
        actionMap.put("skip",new Action(){public void exec(String[] arg, IMessage m) {skip(arg,m);}});
    }
    
    @Override
    protected void initalizeCommData(){
        String[][] comms = {{"[jJ]oin","join","joins a voicechannel.","'dog join [channel name]' to join a specific channel. 'dog join me' to join the channel you are currently on"}
                       ,{"[pP]lay","play","plays a clip.","dog play [clipname]. Clip names can be found by using the list command. Multiple clips can be played in sequence using the following format : 'dog play [clipname] [clipname] [clipname]"}
                       ,{"[pP]arlay","parlay","begins parlay with me, this means you don't have to type 'dog'"}
                       ,{"[uU]nparlay","unparlay","ends parlay with me"}
                       ,{"[lL]ist","list","Lists various things, multi-use command"}
                       ,{"[aA]dd[Cc]lip","addclip","Add clip to soundboard"}
                       ,{"[mM]ove[Aa]ll","moveall","Moves all users from one channel to another"}
                       ,{"[gG]ame","game","Starts a game"}
                       ,{"[gG]uess","guess","Guesses an answer for the current game on this server"}
                       ,{"[dD]elete[cC]lip","deleteclip","Deletes the specified clip"}
                       ,{"[sS]et[vV]olume","setvolume","Sets the volume of the specified clip"}
                       ,{"[bB]an[cC]lip","banclip","Bans a clip depending on the current banning policy, use 'dog list ban' for more info"}
                       ,{"[uU]n[bB]an[cC]lip","unbanclip","Unbans a clip (See banclip)"}
                       ,{"[vV]eto[cC]lip","vetoclip","Vetoes a clip based on the current vetoing policy, use 'dog list ban' for more info"}
                       ,{"[sS]et","set","Sets rules on server (currently only r9k on/off) need admin rights "}
                       ,{"[sS]hutdown","shutdown","shutsdown the bot (this really should be in an admin section)","2"}
                       ,{"[sS]kip","skip","Skips the current clip (no current limitations, please use responsibly)"}


                       
                        };
    
        commData = comms;
    }
    
    @Override
    protected void initalizeEventMethods(){
        
        
        
    }
    

    

    
    private void shutdown(IMessage message){
        if(!isMasterAdmin(message.getAuthor())){
            sendMessage(message.getChannel(), "You must be a master-admin to invoke this command. (This command completely shutsdown the bot, would require sshing back in to reset it.");
            return;
        }
        sendMessage(message.getChannel(),"Bot shutting down");
        System.exit(0);
    }
    
    
    

    
    
    public void parlay(IMessage message){
        IUser user = message.getAuthor();
        for(IUser u : parlayUsers){
            if(u.getID().equals(user.getID())){
                sendMessage(message.getChannel(),"we already talkin'");
                return;
            }
        }
        sendMessage(message.getChannel(),"Ok dog, I'll know you are talking to me now");
        parlayUsers.add(user);
        
    }
    
    public void unparlay(IMessage message){
                IUser user = message.getAuthor();
        for(int i = 0; i<parlayUsers.size(); i++){
            if(parlayUsers.get(i).getID().equals(user.getID())){
                sendMessage(message.getChannel(),"Ok, dog, catch you later.");
                parlayUsers.remove(i);
                return;
            }
        }
        sendMessage(message.getChannel(),"You never had a parlay with me");
    }
    
        
    private void list(String[] arguments, IMessage message) {
   
        String s = "";
        if(arguments.length==0){
            
           sendMessage(message.getChannel(), "Here are my commands dog :");
           
            for(String[] comm : commData){
                s+="_"+comm[1]+"_"+" : `"+comm[2]+"`\n";
                s+="\n";
            }
               
            sendMessage(message.getChannel(),s);
            
            return;
        }
        
    }
    public void skip(String[] args, IMessage message){
        //UserLog ul = userLogs.get(message.getAuthor().getID());
        IGuild guild = message.getGuild();
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        String returnMessage = "";
        if(args.length!=0){
            if(args[0].equals("skip")){
                player.clear();
                returnMessage = message.getAuthor()+" skipped all queued clips";
            }
        }
        else{
            player.skip();
            returnMessage = message.getAuthor().getName()+" skipped the current clip.";
        }
        
        sendMessage(message.getChannel(),returnMessage);
    }
   



    public boolean isMasterAdmin(IUser user) {
        if(DBHandler.getAdminRights(user.getID()).equals("master")){
            return true;
        }
        return false;
    }
    @Override
    public boolean canUse(int adminLevel, IUser user, IGuild guild){
        
        switch (adminLevel) {
            case 0:
                return true;
            case 1:
                return isAdmin(user,guild)||isMasterAdmin(user);
            case 2:
                return isMasterAdmin(user);
            default:
                return false;
        }
        
    }


}
