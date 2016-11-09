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
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
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
                       ,{"[sS]hutdown","shutdown","shutsdown the bot (this really should be in an admin section)"}
                       ,{"[sS]kip","skip","Skips the current clip (no current limitations, please use responsibly)"}


                       
                        };
    
    

    //ArrayList<ArrayList<Thread>> gameThread;
    Thread gameThread;
    
    HashMap<String, HashMap<String, Game>> map;
    
    ArrayList<Float> volumeBuffer;
    
    ArrayList<IUser> parlayUsers;
    
    HashMap<String, UserLog> userLogs;
    
    HashMap<String, HashMap<String, Thread>> threadmap;
    HashMap<String, GuildSettings> guildSettings;
    
    String[] gameList = {"G1","G2","G3","G4","G5"};
    

    
    Track currentTrack;
    
    DjDogServer djdog;
    
    boolean r9k;
    
    Game game;
    
    List<ArrayList<String>> pokemon;

    public WilsonServer(IDiscordClient client, DjDogServer server){
        
        super(client);
        prefix = "dog";
        map = new HashMap<>();
        volumeBuffer = new ArrayList<>();
        parlayUsers = new ArrayList<>();
        djdog = server;
        userLogs = new HashMap<>();
        initalizeCommands();
        initCommChannels();
        commData = comms;
        r9k = false;
        actionMap = new HashMap<>();
        initGuildSettings();
        
    }
    
    
    @EventSubscriber
    public void trackChange(TrackStartEvent event){
        Track t = event.getTrack();
        currentTrack = t;
        String trackName = t.getMetadata().get("name").toString();
        
        IUser banner = checkTrackForChannel(getConnectedChannel(event.getPlayer().getGuild()),t);
        
        if(banner!=null){
            IChannel channel = (IChannel) t.getMetadata().get("textchannel");
            sendMessage(channel, "Clip :"+trackName+" was skipped as "+banner.getName()+" has voted to ban the clip and is present within this channel.");
            currentTrack = null;
            event.getPlayer().skip();
            return;
        }
    
        
        currentTrack = t;
        t.getMetadata().put("played", true);
        AudioPlayer player = event.getPlayer();
        player.setVolume((float) t.getMetadata().get("volume"));
    }
        
    @EventSubscriber
    public void trackEnd(TrackFinishEvent event){
        
        currentTrack = null;
    }
    public void initGuildSettings(){
        List<IGuild> guilds = client.getGuilds();
        for(IGuild g : guilds){
            GuildSettings gs = new GuildSettings(DBHandler.getGuildInfo(g.getID()));
            guildSettings.put(g.getID(), gs);
        }
    }
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

    protected void initalizeActions(){
         actionMap = new HashMap<>();
        
        actionMap.put("join",  new Action(){public void exec(String[] arg, IMessage m) {join(arg,m);}});
        actionMap.put("play",new Action(){public void exec(String[] arg, IMessage m) {play(arg,m);}});
        actionMap.put("parlay",new Action(){public void exec(String[] arg, IMessage m) {parlay(m);}});
        actionMap.put("unparlay",new Action(){public void exec(String[] arg, IMessage m) {unparlay(m);}});
        actionMap.put("list",new Action(){public void exec(String[] arg, IMessage m) {list(arg,m);}});
        actionMap.put("addclip",new Action(){public void exec(String[] arg, IMessage m) {addClip(arg,m);}});
        actionMap.put("moveall",new Action(){public void exec(String[] arg, IMessage m) {moveAll(arg,m);}});
        actionMap.put("game",new Action(){public void exec(String[] arg, IMessage m) {game(arg,m);}});
        actionMap.put("guess",new Action(){public void exec(String[] arg, IMessage m) {guess(arg,m);}});
        actionMap.put("deleteclip",new Action(){public void exec(String[] arg, IMessage m) {deleteClip(arg,m);}});
        actionMap.put("setvolume",new Action(){public void exec(String[] arg, IMessage m) {setVolume(arg,m);}});
        actionMap.put("banclip",new Action(){public void exec(String[] arg, IMessage m) {ban(arg,m);}});
        actionMap.put("unbanclip",new Action(){public void exec(String[] arg, IMessage m) {unban(arg,m);}});
        actionMap.put("vetoclip",new Action(){public void exec(String[] arg, IMessage m) {veto(arg,m);}});
        actionMap.put("set",new Action(){public void exec(String[] arg, IMessage m) {set(arg,m);}});
        actionMap.put("shutdown",new Action(){public void exec(String[] arg, IMessage m) {shutdown(m);}});
        actionMap.put("skip",new Action(){public void exec(String[] arg, IMessage m) {skip(arg,m);}});
    }
    
    protected void initalizeCommData(){
        commData = comms;
    }
    
    private void populateMap(){
        List<IGuild> guilds = client.getGuilds();
        for(IGuild g : guilds){
            HashMap<String, Game> hmap = new HashMap<>();
            map.put(g.getID(), hmap);
        }
    }
    
    private Game getGame(String gid, String thread){
        return map.get(gid).get(thread);
    }
    private Thread getThread(String gid, String thread){
        return threadmap.get(gid).get(thread);
    }
    private void setGame(String gid, String thread, Game g){
        map.get(gid).put(thread, g);
    }
    private void setThread(String gid, String thread, Thread t){
        threadmap.get(gid).put(thread, t);
    }
    
    private void shutdown(IMessage message){
        if(!isMasterAdmin(message.getAuthor())){
            sendMessage(message.getChannel(), "You must be a master-admin to invoke this command. (This command completely shutsdown the bot, would require sshing back in to reset it.");
            return;
        }
        sendMessage(message.getChannel(),"Bot shutting down");
        System.exit(0);
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
    
    @Override
    public void join(String[] arguments, IMessage message){
        
        IChannel channel = message.getChannel();
        
        if(arguments.length==0){
            sendMessage(channel,"Nigga, tell me where you want me to go, give a channel name or use 'me' if you want me to join you");
        }
        String argument = arguments[0];
        for(int i = 1; i<arguments.length; i++){
            argument += " "+arguments[i];
        }
        
        IVoiceChannel voicechannel = null;
        
        if(argument.equals("me")){
           
            for(IVoiceChannel vc : message.getAuthor().getConnectedVoiceChannels()){
                if(vc.getGuild().getID().equals(message.getGuild().getID())){

                        voicechannel = vc;
                        break;

                }
            }
            if(voicechannel==null){
                sendMessage(message.getChannel(),"You are not in a voicechannel");
                return;
            }
        }
        else{
            for(IVoiceChannel vchan : message.getGuild().getVoiceChannels()){

            
                if(vchan.getName().equals(argument)){
                    
                    sendMessage(channel,"On my way to "+argument+", dog");
                    voicechannel = vchan;
                    break;
                }            
            }
            if(voicechannel==null){
                sendMessage(channel,"Stop playing nigga there ain't no "+argument+" channel in this guild.");
                return;
            }
        }
        
        IUser banner = checkTrackForChannel(voicechannel,currentTrack);
        if(banner!=null){
            
            
            sendMessage(channel,currentTrack.getMetadata().get("name").toString()+" was skipped on joining this channel as "+banner.getName()+" has banned it.");
            AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(message.getGuild());
            player.skip();

       }
        
        try {
            voicechannel.join();
        } catch (MissingPermissionsException ex) {
            sendMessage(channel,"I do not have permissions to join this channel.");

        }
        

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
    
    public void setVolume(String[] arguments, IMessage message){
        
        float volume;
        try{
            volume = Float.parseFloat(arguments[1]);
        }
        
        catch(NumberFormatException e){
            sendMessage(message.getChannel(), "You gave me "+arguments[1]+" to convert into a number. I'm afraid I can't do that Dave.");
            return;
        }
        
        if(volume>3){
            sendMessage(message.getChannel(), "You cannot increase a clip's volume by more than 3 times");
            return;
        }
        else if(volume < 0.1){
            sendMessage(message.getChannel(), "You cannot reduce a clip's volume by more than 10 times");
            return;
        }
        
        if(!DBHandler.clipExists(arguments[0])){
            sendMessage(message.getChannel(), "You gave me "+arguments[0]+" as a clip name, Dave. That clip does not exist. I'm afraid I can't reduce the volume for this clip, Dave.");
            return;
        }
        DBHandler.setVolume(arguments[0], volume);
        
        sendMessage(message.getChannel(), arguments[0]+" was set to volume level "+volume);
        
    }
    
    public void play(String[] arguments, IMessage message){
        
        System.out.println(arguments[0]);
        if(arguments[0].equals("random"))
        {
            System.out.println("playing random");
            String[] tags = new String[arguments.length-1];
            
            for(int i = 1; i<arguments.length; i++){
                tags[i-1]=arguments[i];
            }
            System.out.println(Arrays.toString(tags));
            playRandom(tags, message);
        }
        else
        {
            playClip(arguments, message);
        }
      
        
    }
    
    private void playClip(String[] files, IMessage message){
        ArrayList<Clip> clips = new ArrayList<>();
        ArrayList<String[]> clip = DBHandler.getClips();
        
      
        
        for(String s : files){
            if(s.length()>0){
                
                for(String[] clipData: clip){
                    if(clipData[0].equals(s)){
                        clips.add(new Clip(new File("assets/clips/"+clipData[0]+".mp3"), Float.parseFloat(clipData[1])));
                    }
                }
                
                
            }                               
        }

       

        playFiles(clips, message.getGuild(), message.getAuthor(), message.getChannel());

        
    }
    
    public void timeout(String[] args, IMessage message){
        
        if(args.length<2){
            sendMessage(message.getChannel(),"You must give two arguments : [userName] [timeoutduration]");
        }
        
        List<IUser> userlist = message.getGuild().getUsersByName(args[0]);
        if(userlist.size()<1){
            sendMessage(message.getChannel(),"There is no one with the name "+args[0]+" in this channel");
            return;
        }
        else{
            if(userlist.size()>1)
            System.out.println("Warning there are multiple people with the name "+message.getAuthor().getName()+" in this channel");
            
            IUser user = userlist.get(0);
            
            List<IVoiceChannel> vcList = message.getGuild().getVoiceChannelsByName("afk");
            
            try {
                user.moveToVoiceChannel(vcList.get(0));
            } catch (DiscordException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RateLimitException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MissingPermissionsException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void setTag(String[] args, IMessage message){
        
        
        
    }
    public void setTagRegex(String[] args, IMessage message){
        if(!isAdmin(message.getAuthor())){
            sendMessage(message.getChannel(),"You must be an admin to do that. It's not that I don't trust you, I just don't trust you");
            return;
        }
        for(String s: args){
            
        }
    }
    public void getSource(String[] args, IMessage message){
        
    }
    private void playRandom(String[] tags, IMessage message){
        
        ArrayList<String[]> data;
        
        if(tags.length==0){
            data = DBHandler.getClips();
        }
        else{
            data = DBHandler.getClips(tags);
        }
        
        System.out.println("List"+data);
        ArrayList<Clip> clips = new ArrayList<>();
        
        Random r = new Random();
        
        int n = r.nextInt(data.size());
        
        String[] s = data.get(n);
        Clip clip = new Clip(new File("assets/clips/"+s[0]+".mp3"),Float.parseFloat(s[1]));
        sendMessage(message.getChannel(), "Playing random clip");
        
        clips.add(clip);
        

        playFiles(clips, message.getGuild(), message.getAuthor(), message.getChannel());
        
    }
    
    private void playFiles(ArrayList<Clip> clips, IGuild guild, IUser user, IChannel channel){
        
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);

        for(Clip clip : clips){
            try {
                Track track = new Track(new FileProvider(clip.getFile()));
                Map<String, Object> metadata = track.getMetadata();
                metadata.put("volume",clip.getVolume());
                metadata.put("name",clip.getFile().getName().split("\\.")[0]);
                metadata.put("time", LocalTime.now());
                metadata.put("played", false);
                metadata.put("textchannel", channel);
                UserLog log = userLogs.get(user.getID());
                
                if(log == null){
                    log = new UserLog(user,this,guild);
                    userLogs.put(user.getID(), log);
                }
                long n;
                try {
                    n = log.checkTrack(track);
                          
                } catch (R9KException ex) {
                   
                    sendMessage(channel,"R9k mode is in effect, meaning you cannot play the same clip multiple times within a small time frame, please wait "+ex.getDiff()+" seconds to play '"+ex.getName()+"'.");
                    return;
                    
                }
                if(n==0)
                    player.queue(track);
                else if(n<0){
                    sendMessage(channel,"You still have "+(-n)+" unplayed clips in your queue, wait for them to finish before queuing more");
                }
                else{
                    sendMessage(channel,"You are sending too many clips, please wait "+n+" seconds to queue another clip.");
                }
                
            } catch (IOException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    public void deleteClip(String[] arguments, IMessage message){
        if(arguments.length<1){
            sendMessage(message.getChannel(), "Please specify what name clip you want to delete");
            return;
        }
        sendMessage(message.getChannel(), "Deleting clip");
        String clipid =  DBHandler.getClipID(arguments[0]);
        String ownerid = DBHandler.getOwnerID(arguments[0]);
        if(clipid.equals("")){
            sendMessage(message.getChannel(), "I could not find a clip name "+arguments[0]);
            return;
        }
        
        if(!ownerid.equals(message.getAuthor().getID())){
            sendMessage(message.getChannel(), "According to my records you do not own that clip");

            if(isMasterAdmin(message.getAuthor())){
                sendMessage(message.getChannel(), "Oh, I'm very sorry Sir. I did not realise you were an Administrator. The clip will be deleted at once.");
            }
            else{
                return;
            }
        }
        File f = new File("assets/"+arguments[0]+".mp3");
        f.delete();
        DBHandler.deleteClip(clipid);
    }
    
    public void addClip(String[] arguments, IMessage message){
        
        final int maxClipLength = 30;
        sendMessage(message.getChannel(), "Ok dog, I'll try to add that to our soundboard");
        
        final int maxClips = 50;
        
        if(message.getAuthor().getID().equals("128406852059922432")){
            
        }
        else{
            
        ArrayList<Integer> clips = DBHandler.getClips(message.getAuthor().getID());
        if(clips.size()>=maxClips){
            sendMessage(message.getChannel(), "You already have the maximum of "+maxClips+" clips");
            return;
            //sendMessage(message.getChannel(), "To list the clips you own, use command (dog) list clips self. (NOT IMPLEMENTED YET)";
            //sendMessage(message.getChannel(), "To list the clips you own, use command (dog) list clips self. (NOT IMPLEMENTED YET)";
        }
        }
        
        if(arguments.length == 1){
            sendMessage(message.getChannel(), "You must at least give a url and a name, I only recieved 1 argument after addclip from you");
        }
        
        String url = arguments[0];
        String name = arguments[1];
        
        
        String ownerID = DBHandler.getClipID(name);
        if(ownerID!=""){
            if(!message.getAuthor().getID().equals(ownerID)){
                sendMessage(message.getChannel(), "There is already a clip with name "+name+" which you do not own.");
                return;
            }
            else{
                sendMessage(message.getChannel(), "There is already a clip with name "+name+" which you own. Delete the clip first.");
                return;
            }
        }
        
        if(arguments.length<2){
            sendMessage(message.getChannel(), "To add a clip, you have to give 2-4 arguments after the command.");
            sendMessage(message.getChannel(), "The first argument is the url of the clip (can be http or youtube)");
            sendMessage(message.getChannel(), "The second argument is the name you want to give the clip");
            sendMessage(message.getChannel(), "The third and fourth arguments are the start point and end point you want, these are optional. If you only give 1 extra argument it will be assumed as the duration you want the clip to last starting from the beginning");
            return;
        }
        
        File file = null;


        double startpoint = 0;
        double duration = 0;
        int lastIndex = 0;
        String[] times;
        if(arguments.length<4){
             times = new String[1];
        }
        else{
            times = new String[2];
        }

        try{
        
            LocalTime[] realTimes = new LocalTime[2];
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            for(int i = 2; i<arguments.length; i++){
                lastIndex = i;
                String time = arguments[i];
                String[] ts = time.split(":");
                int h = 0, m = 0;
                long s = 0, ms = 0;
                
                if(ts.length>0){
                    double se = Double.parseDouble(ts[ts.length-1]);
                    s = Math.round(se);
                    ms = Math.round((se-s)*1000);
                }
                if(ts.length>1){
                    m = Integer.parseInt(ts[ts.length-2]);
                }
                if(ts.length>2){
                    h = Integer.parseInt(ts[0]);
                }
                
                
                
                realTimes[i-2] = LocalTime.of((int) h,(int)m,(int)s,(int)ms);
            }
            
         
           
           
           if(times.length>1){
               LocalTime diff = realTimes[1].minusNanos(realTimes[0].toNanoOfDay());
               times[0] = realTimes[0].format(dtf);
               times[1] = diff.format(dtf);
               duration = diff.toSecondOfDay();
           }
           else{
               times[0] = realTimes[0].format(dtf);
               duration = realTimes[0].toSecondOfDay();
           }
            
           

        }
        catch(NumberFormatException e ){
            
            sendMessage(message.getChannel(), "An invalid time was given for one of the arguments. Format must be 'HH:MM:SS' for example 10:30:2 or 30:2 \n "+arguments[lastIndex]+" was invalid");
        }
        
       
        
        
        if(duration<0){
            sendMessage(message.getChannel(), "Optional 3rd and 4th arguments must be [startpoint] [endpoint], it's likely you did [endpoint] [startpoint]");
            return;
        }
        if(duration>maxClipLength){
            sendMessage(message.getChannel(), "duration of clip cannot be above "+maxClipLength+" seconds");
            return;
        }
        
        //sendMessage(message.getChannel(),"Your request paperwork seems to be in order, I'll try to download the file now.");
        sendMessage(message.getChannel(),"Starting download, I'll tell you when I'm done. I won't be able to do any other requests in this time period cause my creator is lazy");
        
        boolean checkLength = false;
        
        if(times==null){
            checkLength = true;
        }
        
        try {
            file = Downloader.download(url, name, message.getChannel(), checkLength);
        } catch (IOException ex) {
            sendMessage(message.getChannel(), "There was an error downloading the requested file.");
            return;
        } catch (InterruptedException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        sendMessage(message.getChannel(),"File downloaded, trimming and adding to soundboard now.");
        sendMessage(message.getChannel(),"If file is http and over "+maxClipLength+" seconds, it will be automatically trimmed to that.");
        
        if(times == null){
            times = new String[1];
            times[0] = ""+maxClipLength;
        }
        
        try {
            Main.ffmpeg.convertAndTrim(file, name, times, "clips/");
        } catch (IOException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      
        
        int clipID = DBHandler.addClip(name, startpoint, duration, url, message.getAuthor().getID());
        DBHandler.addClipToUser(clipID, message.getAuthor().getID());
        
        
        
        

        System.out.println(startpoint+" + "+duration);
        
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
        for(String arg  : arguments){
            if(matches(arg,"\\S+")){
                arg = arg.substring(m.start(),m.end());
                if(arg.equals("clips")){
                    listClips(message);
                }
            }
        }   
        
    }
    public void set(String[] arguments, IMessage message){
        
        if(!isAdmin(message.getAuthor())){
            sendMessage(message.getChannel(), "You are not an admin");
            return;
        }
        if(arguments.length<2){
            return;
        }
        
        if(arguments[0].equals("r9k")){
            
            if(arguments[1].equals("on")){
                sendMessage(message.getChannel(), "r9k mode is enabled");
                r9k = true;
                return;
            }
            else if(arguments[1].equals("off")){
                sendMessage(message.getChannel(), "r9k mode is disabled");
                r9k = false;
                return;
            }
        }
        if(arguments[0].equals("admin")){
            if(!isMasterAdmin(message.getAuthor())){
                sendMessage(message.getChannel(), "You must be a master admin to give admin to other users");
                return;
            }
            List<IUser> tempUsers = message.getGuild().getUsersByName(arguments[1]);
            if(tempUsers.size()<1){
                sendMessage(message.getChannel(), "There is noone with that name in this guild");
                return;
            }
            if(tempUsers.size()>1){
                sendMessage(message.getChannel(), "There is more than one person with that name");
                return;
            }
            DBHandler.addAdminRights(tempUsers.get(0).getID(), prefix);
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
    public void game(String[] arguments, IMessage message){

        if(arguments[0].equals("start")){
            
            
            
            
            
            if(arguments[1].equals("pokemon")){
                
                
                
                if(gameThread != null){
                    if(gameThread.isAlive()){
                        sendMessage(message.getChannel(), "There is already a game running on this server");
                        return;
                    }
                }
               boolean b;
                if(arguments.length<3){
                    b = false;
                }
                else{
                b = arguments[3].equals("true");
                }
               
                game = new PokemonGuessGame(Integer.parseInt(arguments[2]), b, message,this);
                gameThread = new Thread(game);
                gameThread.start();
            }
        }
        if(arguments[0].equals("end")){
            game.endGame();
            try {
                gameThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendMessage(message.getChannel(), "Game ended");
        }
         
        
    }
    
    public void guess(String[] arguments, IMessage channel){
        
        PokemonGuessGame g = (PokemonGuessGame) game;
        g.guessPokemon(arguments[0]);
           
        
    }
    public static void recieveFile(IChannel channel, File f){
        
    }
    public void moveAll(String[] arguments, IMessage message){
       // sendMessage(message.getChannel(), "Move all");
        if(arguments.length < 2){
            sendMessage(message.getChannel(), "You must give two arguments dog, first is the channel origin second is the channel destination");
        }
        boolean flag = false;
        for(int i = 0; i<arguments.length; i++){
            if(arguments[i].equals("me")){
                List<IVoiceChannel> channels = message.getAuthor().getConnectedVoiceChannels();
                IVoiceChannel chan = null;
                for(IVoiceChannel c : channels)
                    if(c.getGuild().getID().equals(message.getID()))
                            chan = c;
                if(chan!=null){
                    arguments[i] = chan.getName();
                }
            }
        }
        System.out.println(arguments[0]);
        List<IVoiceChannel> list = message.getGuild().getVoiceChannels();
        
        List<IUser> userList = message.getGuild().getUsers();
        
        IVoiceChannel target = null;
        flag = true;
         for(IVoiceChannel channel : list){
                
            if(channel.getName().replace(" ", "").equals(arguments[1])){
                
                target = channel;
                flag = false;
                break;
                
                
            }


            
        }
        if(flag){
            sendMessage(message.getChannel(),"Could not find a channel called "+arguments[1]+".");
            return;
        }
        
         if(target == null){
             sendMessage(message.getChannel(), "That target channel does not exist playa.");
         }
         
        flag = false;
        for(IUser user : userList){
            List<IVoiceChannel> channels = user.getConnectedVoiceChannels();
            IVoiceChannel chan = null;
            
            for(IVoiceChannel channel: channels){
                if(channel.getGuild().getID().equals(message.getGuild().getID())){
                    chan = channel;
                }
            }
            
            if(chan != null){
                try {
                    IVoiceChannel vchan = chan;
                    if(vchan.getName().replace(" ", "").equals(arguments[0].replace(" ", "")))
                        user.moveToVoiceChannel(target);
                } catch (DiscordException ex) {
                    Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MissingPermissionsException ex) {
                    Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RateLimitException ex) {
                    Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
            }
            else{
                flag = true;
            }
        }

        
        
       
    }
    public void listClips(IMessage message) {
       
        String s = "";
        sendMessage(message.getChannel(),"Here are the clips I know dog.");
        
         File folder = new File("assets");
        
       
         for (String file : DBHandler.getClipNames()){ 
            
                s += file+"\n";
                int n = s.length();
                if(n>1500){
                    sendMessage(message.getChannel(),"```"+s+"```");
                    s="";
                }
         }
         

         
         sendMessage(message.getChannel(),"```"+s+"```");

        
    }

   
    public void ban(String[] arguments, IMessage message){
        IChannel channel = message.getChannel();
        ArrayList<String> exist = new ArrayList<>();
        ArrayList<String> already = new ArrayList<>();
        ArrayList<String> success = new ArrayList<>();
        String userID = message.getAuthor().getID();
        for(String s: arguments){
            
            if(!DBHandler.clipExists(s)){
                exist.add(s);
            }
            else
            {
                ArrayList<String> banners = DBHandler.getBanners(s);
                boolean flag = false;
                for(String banner: banners){
                    if(banner.equals(userID)){
                        already.add(s);
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    DBHandler.banClip(s, message.getAuthor().getID());
                    success.add(s);
                }
            
            
            }
        }
        
        if(success.size()>0)
            sendMessage(channel,"The following clips were successfully banned : \n"+success);        
        if(exist.size()>0)
            sendMessage(channel,"The following clips did not exist and were therefore not banned : \n"+exist);
        if(already.size()>0)
            sendMessage(channel,"The following clips have already been banned by you : \n"+already);
        
    }
    public void unban(String[] arguments, IMessage message){
        IChannel channel = message.getChannel();
        ArrayList<String> errors = new ArrayList<>();
        ArrayList<String> success = new ArrayList<>();
        for(String s: arguments){
            
            if(!DBHandler.clipExists(s)){
                errors.add(s);
            }
            else
            {
                DBHandler.unbanClip(s, message.getAuthor().getID());
                success.add(s);
            
            }
        }
        sendMessage(channel,"The following clips were successfully unbanned : \n"+success);
        
        if(errors.size()>0)
        sendMessage(channel,"The following clips did not exist and were therefore not banned : \n"+errors);
    }
    public void veto(String[] arguments, IMessage message){
        sendMessage(message.getChannel(),"ERROR VETOING IS NOT IMPLEMENTED. REASON GIVEN : 'can't be fucked rite now'");
    }

    @Override
    public boolean isAdmin(IUser user) {
        if(DBHandler.getAdminRights(user.getID()).equals("null")){
            return false;
        }
        return true;
    }

    public boolean isMasterAdmin(IUser user) {
        if(DBHandler.getAdminRights(user.getID()).equals("master")){
            return true;
        }
        return false;
    }

    protected void initCommChannels(){
        List<IGuild> guilds = client.getGuilds();
        for(IGuild guild : guilds){
            setCommChannel(guild);
        }
    }
    protected void setCommChannel(IGuild guild){
        
        List<IChannel> channels = guild.getChannels();
        for(IChannel channel: channels){
            
            if(channel.getName().equals(defCommChanName)){
                setCommChannel(channel);
                return;
            }
            
        }
        
        
    }
    protected void setCommChannel(IChannel channel){
        this.commChanMap.put(channel.getGuild().getID(), channel);
    }
}
