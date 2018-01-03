/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.CoreAction;
import abanstudio.discordbot.BotServer;
import abanstudio.discordbot.Main;
import abanstudio.discordbot.wilson.Clip;
import abanstudio.discordbot.wilson.UserLog;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.exceptions.R9KException;
import abanstudio.utils.Downloader;
import abanstudio.utils.sqlite.DBHandler;
import java.io.File;
import java.io.IOException;
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
import javax.sound.sampled.UnsupportedAudioFileException;
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
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author Reetoo
 */
public class Soundboard extends Module{
    
    Track currentTrack;
    HashMap<String, UserLog> userLogs;
    public boolean r9k = false;
        
    public Soundboard(BotServer server){
        super(server);
        userLogs = new HashMap<>();
    }
    public IVoiceChannel getConnectedChannel(IGuild guild){
        List<IVoiceChannel> channels = client.getConnectedVoiceChannels();
        for(IVoiceChannel channel : channels){
            if(channel.getGuild().getStringID().equals(guild.getStringID())){
                return channel;
            }
        }
        System.out.println("CRITICAL ERROR");
        return null;
    }
     public void join(String[] arguments, IMessage message){
        IChannel channel = message.getChannel();
        
        if(arguments.length==0){
            sendMessage(channel,"Tell me where you want me to go, give a channel name or use 'me' if you want me to join you");
        }
        String argument = arguments[0];
        for(int i = 1; i<arguments.length; i++){
            argument += " "+arguments[i];
        }
        
        if(argument.equals("me")){

            IVoiceChannel vc = message.getAuthor().getVoiceStateForGuild(message.getGuild()).getChannel();
            if(vc == null) {
                try {
                    vc.join();
                } catch (MissingPermissionsException ex) {

                    sendMessage(message.getChannel(), "I don't have permissions to join that channel");

                }
            }
            else {


                sendMessage(message.getChannel(), "You are not in a voicechannel");
                return;
            }
        }
        for(IVoiceChannel vchan : message.getGuild().getVoiceChannels()){

            
            if(vchan.getName().equals(argument)){
                sendMessage(channel,"On my way to "+argument+", dog");
                try {
                    vchan.join();
                } catch (MissingPermissionsException ex) {
                    sendMessage(message.getChannel(),"I don't have permissions to join that channel");
                }
                return;
            }
            
        }
        sendMessage(channel,"Stop playing there ain't no "+argument+" channel in this guild.");
        

        
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

                    if(id.equals(u.getStringID())){
                        
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
        overrideMap = new HashMap<>();
        
        overrideMap.put("join", new Action(){
            @Override
            public void exec(String[] arguments, IMessage message) {
                join(arguments, message);
            }
        });
        actionMap.put("play",new Action(){public void exec(String[] arg, IMessage m) {play(arg,m);}});
        actionMap.put("addclip",new Action(){public void exec(String[] arg, IMessage m) {addClip(arg,m);}});
        actionMap.put("deleteclip",new Action(){public void exec(String[] arg, IMessage m) {deleteClip(arg,m);}});
        actionMap.put("setvolume",new Action(){public void exec(String[] arg, IMessage m) {setVolume(arg,m);}});
        actionMap.put("banclip",new Action(){public void exec(String[] arg, IMessage m) {ban(arg,m);}});
        actionMap.put("unbanclip",new Action(){public void exec(String[] arg, IMessage m) {unban(arg,m);}});
        actionMap.put("vetoclip",new Action(){public void exec(String[] arg, IMessage m) {veto(arg,m);}});
        overrideMap.put("list", new Action(){public void exec(String[] arg, IMessage m) {list(arg,m);}});
 
        
    }

    @Override
    protected void initalizeCommData() {
        String[][] comms = {{"[jJ]oin", "join", "joins a channel, will skip any clips currently banned by users within the channel"}};
        
        commData = comms;
    }
    
    public void ping(String[] arg, IMessage m){
        server.sendMessage(m.getChannel(), "pong");
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
        server.sendMessage(message.getChannel(), "Playing random clip");
        
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
                UserLog log = userLogs.get(user.getStringID());
                
                if(log == null){
                    log = new UserLog(user,this,guild);
                    userLogs.put(user.getStringID(), log);
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
        
        if(!ownerid.equals(message.getAuthor().getStringID())){
            sendMessage(message.getChannel(), "According to my records you do not own that clip");

            if(server.canUse(2,message.getAuthor(),message.getGuild())){
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
        public void listRecentClips(IMessage message, int num){
          String s = "";
        sendMessage(message.getChannel(),"Here are the most recent "+num+" clips I know.");
        
         File folder = new File("assets");
        
       
         for (String[] file :  DBHandler.getClips(num)){ 
            
                s += file[0]+"\n";
                int n = s.length();
                if(n>1500){
                    sendMessage(message.getChannel(),"```"+s+"```");
                    s="";
                }
         }
         

         
         sendMessage(message.getChannel(),"```"+s+"```");
    }
    public void listRecentClips(IMessage message){
          String s = "";
        sendMessage(message.getChannel(),"Here are the recent clips I know.");
        
         File folder = new File("assets");
        
       
         for (String[] file :  DBHandler.getClips(10)){ 
            
                s += file[0]+"\n";
                int n = s.length();
                if(n>1500){
                    sendMessage(message.getChannel(),"```"+s+"```");
                    s="";
                }
         }
         

         
         sendMessage(message.getChannel(),"```"+s+"```");
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
            if(server.matches(arg,"\\S+")){
                arg = arg.substring(server.matcher.start(),server.matcher.end());
                if(arg.equals("clips")){
                    listClips(message);
                }
            }
        }   
        
    }

    public void listClips(IMessage message, String[] tags){
        String s = "";
        sendMessage(message.getChannel(),"Here are the clips I know with the tag '"+tags[0]+"'.");
        
         File folder = new File("assets");
        
       
         for (String[] file :  DBHandler.getClips(tags)){ 
            
                s += file[0]+"\n";
                int n = s.length();
                if(n>1500){
                    sendMessage(message.getChannel(),"```"+s+"```");
                    s="";
                }
         }
         

         
         sendMessage(message.getChannel(),"```"+s+"```");
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
        String userID = message.getAuthor().getStringID();
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
                    DBHandler.banClip(s, message.getAuthor().getStringID());
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
                DBHandler.unbanClip(s, message.getAuthor().getStringID());
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

    
  

    public void addClip(String[] arguments, IMessage message){

        final int maxClipLength = 30;
        sendMessage(message.getChannel(), "Ok dog, I'll try to add that to our soundboard");
        
        final int maxClips = 50;
        
        if(message.getAuthor().getStringID().equals("128406852059922432")){
            
        }
        else{
            
        ArrayList<Integer> clips = DBHandler.getClips(message.getAuthor().getStringID());
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
        
        
        String ownerID = DBHandler.getOwnerID(name);
        if(ownerID!=""){
            if(!message.getAuthor().getStringID().equals(ownerID)){
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
        if(arguments.length<3){
             times=null;
        }
        else if(arguments.length<4){
            times = new String[1];
        }
        else{
            times = new String[2];
        }
        if(arguments.length<3){
            
        }
        else
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
                    ms = Math.round((s-se)*1000);
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
        
      
        
        int clipID = DBHandler.addClip(name, startpoint, duration, url, message.getAuthor().getStringID());
        DBHandler.addClipToUser(clipID, message.getAuthor().getStringID());
        
        
        
        

        System.out.println(startpoint+" + "+duration);
        
    }
    
    @Override
    public String getName() {
        return "Soundboard";
    }
    
    public void onReady(){
        
    }
   

}
