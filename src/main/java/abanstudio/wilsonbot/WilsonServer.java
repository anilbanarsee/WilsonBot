/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import abanstudio.djdog.DjDogServer;
import abanstudio.utils.sqlite.DBHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.VoiceChannel;
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

/**
 *
 * @author Reetoo
 */
public class WilsonServer {
    
    IDiscordClient client;
    
    String[][] commMap = {{"\\b[jJ]oin\\b","join","joins a voicechannel.","'dog join [channel name]' to join a specific channel. 'dog join me' to join the channel you are currently on"}
                       ,{"\\b[pP]lay\\b","play","plays a clip.","dog play [clipname]. Clip names can be found by using the list command. Multiple clips can be played in sequence using the following format : 'dog play [clipname] [clipname] [clipname]"}
                       ,{"\\b[pP]arlay\\b","parlay","begins parlay with me, this means you don't have to type 'dog'"}
                       ,{"\\b[uU]nparlay\\b","unparlay","ends parlay with me"}
                       ,{"\\b[lL]ist\\b","list","Lists various things, multi-use command"}
                       ,{"\\b[aA]dd[Cc]lip\\b","addclip","Add clip to soundboard"}
                       ,{"\\b[mM]ove[Aa]ll\\b","moveall","Moves all users from one channel to another"}
                       ,{"\\b[gG]ame\\b","game","Starts a game"}
                       ,{"\\b[gG]uess\\b","guess","Guesses an answer for the current game on this server"}
                       ,{"\\b[dD]elete[cC]lip\\b","deleteclip","Deletes the specified clip"}
                       ,{"\\b[sS]et[vV]olume\\b","setvolume","Sets the volume of the specified clip"}
                       ,{"\\b[aA]dd[mM]usic\\b","addmusic","Adds music clip to bot"}
                       
                        };
    
    static Matcher m;
    //ArrayList<ArrayList<Thread>> gameThread;
    Thread gameThread;
    
    HashMap<String, HashMap<String, Game>> map;
    
    ArrayList<Float> volumeBuffer;
    
    HashMap<String, HashMap<String, Thread>> threadmap;
    
    String[] gameList = {"G1","G2","G3","G4","G5"};
    
    DjDogServer djdog;
    
    Game game;
    
    List<ArrayList<String>> pokemon;

    public WilsonServer(IDiscordClient client, DjDogServer server){
        map = new HashMap<>();
        this.client = client;
        volumeBuffer = new ArrayList<>();
        djdog = server;
    }
    
    
    @EventSubscriber
    public void onReady(ReadyEvent event){
        System.out.println("Bot Ready !");
    }
    
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
       ;
        if(event.getMessage().getAuthor().isBot())
            return;
        String message = event.getMessage().getContent();
        boolean flag = false;
        for(IUser u : Main.users){
            if(u.getID().equals(event.getMessage().getAuthor().getID())){
                flag = true;
                break;
            }
        }
        System.out.println(event.getMessage().getChannel().isPrivate());
        
        if(message.startsWith("dog ")||flag){
            String command = message;
            if((message.startsWith("dog ")))
                command = message.substring(4);
            parseCommand(command, event.getMessage());
           
        }
        
            
        
    }
    
    @EventSubscriber
    public void onDisconnect(DiscordDisconnectedEvent event) throws DiscordException{
        System.out.println("Bot disconnected with reason "+event.getReason()+". Reconnecting...");
        Main.main(null);
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
    
    private void accessQueue(){
        ArrayList<String> requests;
        
    }
    
    private void addMusic(String[] arguments, IMessage message)
    {
        File f = null;
        
        System.out.println("ADDMUSIC");
        
        sendMessage(message.getChannel(),"Yo, djdog, add this shit to your music collection");
        
        try {
            djdog.download(arguments, message);
        } catch (IOException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private String matchCommand(String command) {
       

        for(String[] regComm : commMap){
            if(matches(command,regComm[0]))
                return regComm[1];
        }
        
        
        return "null";
    }
    
    public void parseCommand(String command, IMessage message){
         
       String[] split = command.split("\\s+");
       String com = "";
       String arg = "";
       for(int i=0; i<split.length; i++){
           String test = matchCommand(split[i]);
           
           if(!test.equals("null")){
               doCommand(com,arg,message);
               com = test;
               arg = "";
           }
           else{
               arg += " "+split[i];
           }
           
           
       }
       doCommand(com,arg,message);
       
    }
    
    public void doCommand(String command, String argument, IMessage message){
        String[] arguments = argument.split("\\s+");
        int i = arguments.length;
        for(String s : arguments){
            s = s.replace("\\s+", "");
            if(s.equals("")) i--;
        }
        int x = 0;
        String[] newarg = new String[i];
        for(String s: arguments){
            if(!s.equals("")){
                newarg[x] = s;
                x++;
            }
        }
        
        System.out.println(Arrays.toString(newarg));
        if(command.equals("join")){
            //System.out.println("Join "+argument);
            join(newarg,  message);

        }
        else if(command.equals("play")){
            play(newarg, message);
        }
        else if(command.equals("parlay")){
            parlay(message);
        }
        else if(command.equals("unparlay")){
            unparlay(message);
        }
        else if(command.equals("list")){
            list(newarg, message);
        }
        else if(command.equals("download")){
            download(argument, message);
        }
        else if(command.equals("addclip")){
            addClip(newarg, message);
        }
        else if(command.equals("moveall")){
            moveAll(newarg, message);
        }
        else if(command.equals("game")){
            game(newarg, message);
        }
        else if(command.equals("guess")){
            guess(newarg, message);
        }
        else if(command.equals("deleteclip")){
            deleteClip(newarg, message);
        }
        else if(command.equals("setvolume")){
            setVolume(newarg, message);
        }
        else if(command.equals("addmusic")){
            addMusic(newarg, message);
        }
        else if(command.equals("music")){
            music(newarg, message);
        }
    }
    
    public void parlay(IMessage message){
        IUser user = message.getAuthor();
        for(IUser u : Main.users){
            if(u.getID().equals(user.getID())){
                sendMessage(message.getChannel(),"Nigga, we already talkin'");
                return;
            }
        }
        sendMessage(message.getChannel(),"Ok dog, I'll know you are talking to me now");
        Main.users.add(user);
        
    }
    
    public void unparlay(IMessage message){
                IUser user = message.getAuthor();
        for(int i = 0; i<Main.users.size(); i++){
            if(Main.users.get(i).getID().equals(user.getID())){
                sendMessage(message.getChannel(),"Ok, dog, catch you later.");
                Main.users.remove(i);
                return;
            }
        }
        sendMessage(message.getChannel(),"You never had a parlay with me nigga");
    }
    
    public void music(String[] arguments, IMessage message){
        
        try {
            djdog.play(arguments, message);
        } catch (DiscordException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
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
            sendMessage(message.getChannel(), "You cannot increase a clip's volume by more than 4 times");
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
        
        File folder = new File("assets");
        
        for(String s : files){
            if(s.length()>0){
                
                for(String[] clipData: clip){
                    if(clipData[0].equals(s)){
                        clips.add(new Clip(new File("assets/"+clipData[0]+".mp3"), Float.parseFloat(clipData[1])));
                    }
                }
                
                
            }                               
        }

       
        playFiles(clips, message.getGuild());
        
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
        Clip clip = new Clip(new File("assets/"+s[0]+".mp3"),Float.parseFloat(s[1]));
        sendMessage(message.getChannel(), "Playing random clip");
        
        clips.add(clip);
        
        playFiles(clips, message.getGuild());
        
    }
    
    private void playFiles(ArrayList<Clip> clips, IGuild guild){
        
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
                for(Clip clip : clips){
            try {
                player.setVolume(clip.getVolume());
                player.queue(clip.getFile());
                
            } catch (IOException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void join(String[] arguments, IMessage message){
        IChannel channel = message.getChannel();
        
        if(arguments.length==0){
            sendMessage(channel,"Nigga, tell me where you want me to go, give a channel name or use 'me' if you want me to join you");
        }
        String argument = arguments[0];
        for(int i = 1; i<arguments.length; i++){
            argument += " "+arguments[i];
        }
        
        if(argument.equals("me")){
           
            for(IVoiceChannel vc : message.getAuthor().getConnectedVoiceChannels()){
                if(vc.getGuild().getID().equals(message.getGuild().getID())){
                    try {
                        vc.join();
                    } catch (MissingPermissionsException ex) {
                        sendMessage(message.getChannel(),"I don't have permissions to join that channel");
                    }
                    return;
                }
            }
            sendMessage(message.getChannel(),"You are not in a voicechannel");
            return;
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
        sendMessage(channel,"Stop playing nigga there ain't no "+argument+" channel in this guild.");
        

        
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
            return;
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
        
        

        int startpoint = 0;
        int duration = 0;
        int[] times = null;
        try{
        
            if(arguments.length>2){
            String start = arguments[2];
            String[] time = start.split(":");
        
            int seconds = 0;
            if(time.length==3){
                seconds += Integer.parseInt(time[0])*3600;
                seconds += Integer.parseInt(time[1])*60;
                seconds += Integer.parseInt(time[2]);
            }
            else if(time.length==2){
                seconds += Integer.parseInt(time[0])*60;
                seconds += Integer.parseInt(time[1]);

            }
            else if(time.length==1){
                seconds += Integer.parseInt(time[0]);
            }
            duration = seconds;
            times = new int[1];
            times[0] = duration;
            
            if(arguments.length>3){
                startpoint = seconds;
                            start = arguments[3];
            time = start.split(":");
            seconds = 0;
            if(time.length==3){
                seconds += Integer.parseInt(time[0])*3600;
                seconds += Integer.parseInt(time[1])*60;
                seconds += Integer.parseInt(time[2]);
            }
            else if(time.length==2){
                seconds += Integer.parseInt(time[0])*60;
                seconds += Integer.parseInt(time[1]);

            }
            else if(time.length==1){
                seconds += Integer.parseInt(time[0]);
            }
                duration = seconds-startpoint;
                times = new int[2];
                times[0] = startpoint;
                times[1] = duration;
            }
            
            }
        }
        
        catch(NumberFormatException e ){
            sendMessage(message.getChannel(), "An invalid time was given for one of the arguments. Format must be 'HH:MM:SS' for example 10:30:2");
        }
        
        if(duration<0){
            sendMessage(message.getChannel(), "Optional 3rd and 4th arguments must be [startpoint] [endpoint], it's likely you did [endpoint] [startpoint]");
            return;
        }
        if(duration>maxClipLength){
            sendMessage(message.getChannel(), "duration of clip cannot be above 10 seconds");
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
            times = new int[1];
            times[0] = maxClipLength;
        }
        
        Main.ffmpeg.convertAndTrim(file, name, times, "");
        
      
        
        int clipID = DBHandler.addClip(name, startpoint, duration, url, message.getAuthor().getID());
        DBHandler.addClipToUser(clipID, message.getAuthor().getID());
        
        
        
        

        System.out.println(startpoint+" + "+duration);
        
    }
    
    public void leave(IMessage message){
        
    }
    
    public static void sendMessage(IChannel channel, String message){

        RequestBuffer.request(() -> {
		try {
			new MessageBuilder(Main.wilsonClient).withChannel(channel).withContent(message).build();
		} catch (DiscordException | MissingPermissionsException e) {
			e.printStackTrace();
		}
		return null;
	});

    }
    
    public static boolean matches(String s, String regex){
            Pattern p = Pattern.compile(regex);
            m = p.matcher(s);
            
            return m.find();
    }

    private void list(String[] arguments, IMessage message) {
   
        String s = "";
        if(arguments.length==0){
            
           sendMessage(message.getChannel(), "Here are my commands dog :");
           
            for(String[] comm : commMap){
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
               
                game = new PokemonGuessGame(Integer.parseInt(arguments[2]), b, message);
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
    
    public static void sendFile(IChannel channel, File f){
        try {
            channel.sendFile(f);
        } catch (IOException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MissingPermissionsException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DiscordException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RateLimitException ex) {
            Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            
         }
         
         sendMessage(message.getChannel(),"```"+s+"```");

        
    }
    
    private void download(String argument, IMessage message) {
        
        
        
    }


}
