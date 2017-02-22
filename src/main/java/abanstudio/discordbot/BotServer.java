/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import abanstudio.discordbot.Main;
import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.exceptions.InvalidSearchException;
import abanstudio.module.Module;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageEmbedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.Role;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author User
 */
abstract public class BotServer{
    
    
    protected ArrayList<Command> commands;
    public IDiscordClient client;
    protected ArrayList<IRole> roles;
    protected Matcher m;
    protected String[][] commData;
    protected HashMap<String, Action> actionMap;        
    protected HashMap<String, IChannel> commChanMap;
    protected HashMap<String, Module> modules;
    protected String defCommChanName = "botcommands";

    
    
    public String prefix;
    
    public BotServer(IDiscordClient client){
        this.client = client;
    }
    
    @EventSubscriber
    public void onReady(ReadyEvent event){
        System.out.println("Bot Ready !");
    }
    
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
       
        if(event.getMessage().getAuthor().isBot())
            return;
        String message = event.getMessage().getContent();
        
        if(message.startsWith(prefix+" ")){
            
           
            String command = message;
            parseCommand(command, event.getMessage());
           
        }



            
        
    }
    
    @EventSubscriber
    public void onDisconnect(DiscordDisconnectedEvent event) throws DiscordException{
        System.out.println("Bot disconnected with reason "+event.getReason()+". Reconnecting...");
    }
    
    protected abstract void initalizeActions();
    protected abstract void initalizeCommData();
    
    protected void initalizeCommands(){
        
        initalizeActions();
        initalizeCommData();
        
        commands = new ArrayList<>();
        
        for(String[] array : commData){
            
            commands.add(new Command(actionMap.get(array[1]),array));
                
        }
                    
    }
    
    public void onFileRecieved(MessageEmbedEvent event){
        System.out.println("File Recieved Event");
    }
    
    public void parseCommand(String input, IMessage message){
        
        input = input.substring(4);
        String[] split = input.split(" ");
        Command loaded = null;
        ArrayList<String> args = new ArrayList<String>();
        for(String s : split){
            Command c = Command.matchCommand(commands, s);
            
            if(c!=null){
                if(loaded!=null){
                    String[] array = new String[args.size()];
                    loaded.getAction().exec(args.toArray(array), message);
                    args.clear();
                }
                loaded = c;
            }
            else{
                args.add(s);
            }
        }
        String[] array = new String[args.size()];
        loaded.getAction().exec(args.toArray(array), message);
        
    }
    
    public void addModule(Module module){
        ArrayList<Command> commands = module.getCommands();
        int commAdded = 0;
        int commRejected = 0;
        ArrayList<Command> rejectedCommands = new ArrayList<>();
        module.setServer(this);
        for(Command cX: commands){
            boolean flag = true;
            for(Command cY: this.commands){ 
                if(cX.getComm().equals(cY.getComm())){
                    System.out.println("Error : Module "+module.getName()+" tried to add command "+cX.getComm()+" which was already present. Skipping this command, module will still be added.");
                    flag = false;
                    commRejected++;
                    break;
                }
            }
            if(flag){
                this.commands.add(cX);
                commAdded++;
            }
        }
        System.out.println("Loaded module '"+module.getName()+"'. Commands added :"+commAdded+". Commands rejected due to merge conflicts :"+commRejected);
    }
    
    public boolean matches(String s, String regex){
            Pattern p = Pattern.compile(regex);
            m = p.matcher(s);
            
            return m.find();
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
    
    public void leave(IMessage message){
        String guildID = message.getGuild().getID();
        
        IVoiceChannel vc;
        try {
            vc = getVoiceChannel(guildID);
        } catch (InvalidSearchException ex) {
            return;
        }
        
        vc.leave();
        
    }
    
    public IVoiceChannel getVoiceChannel(String guildID) throws InvalidSearchException{
        
        List<IVoiceChannel> channels = client.getConnectedVoiceChannels();
        
        for(IVoiceChannel chan : channels){
            if(chan.getGuild().getID().equals(guildID)){
                return chan;
            }
        }
        
        throw new InvalidSearchException();
        
    }
    public static void sendFile(IChannel channel, File f){
        
        try {
            channel.sendFile(f);
        } catch (IOException | MissingPermissionsException | RateLimitException | DiscordException ex) {
            Logger.getLogger(BotServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    protected void initCommChannels(){
        commChanMap = new HashMap<>();
        List<IGuild> guilds = client.getGuilds();
        for(IGuild guild : guilds){
            setCommChannel(guild);
        }
    }
    protected void setCommChannel(IGuild guild){
        
        List<IChannel> channels = guild.getChannels();
        boolean set = false;
        
        for(IChannel channel: channels){
            
            
            if(channel.getName().equals(defCommChanName)){
                commChanMap.put(guild.getID(), channel);
                return;
            }
        }
        
        if(!set){
            commChanMap.put(guild.getID(), null);
        }     
        
        
    }
    public static boolean canUse(int adminLevel, IUser user, IGuild guild){
        
        List<IRole> roles = guild.getRolesForUser(user);
        
        for(IRole role: roles){
            
            
            for(Permissions p : role.getPermissions()){
                p.hasPermission(Permissions.ADMINISTRATOR.ordinal());
            }
            
        }
        return true;
        
    }
    protected void setTimeoutChannel(IGuild guild, String name){
        
                
    }
    public abstract boolean isAdmin(IUser user);
}
