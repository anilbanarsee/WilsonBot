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
import java.lang.reflect.InvocationTargetException;
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
    protected ArrayList<Module> modules;
    protected Module onMessageOverride = null;

    
    
    public String prefix;
    
    public BotServer(IDiscordClient client){
        this.client = client;
        modules = new ArrayList<>();
    }
    
    @EventSubscriber
    public void onReady(ReadyEvent event){
        
        onServerReady(event);
        System.out.println("Modules setting up...");
        for(Module m: modules){
            System.out.println("["+m+"]");
            m.onReady();
            System.out.println("Module :"+m+", ready");
        }
        System.out.println("Modules ready");
        System.out.println("Setup complete");
        

    }
    protected abstract void onServerReady(ReadyEvent event);
    
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
       
        if(this.onMessageOverride!=null){
            try {
               
                    onMessageOverride.getClass().getMethod("onMessage", MessageReceivedEvent.class).invoke(onMessageOverride,event);

            } catch (NoSuchMethodException | SecurityException  | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(BotServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            if(event.getMessage().getAuthor().isBot())
                return;
            String message = event.getMessage().getContent();
        
            if(message.startsWith(prefix+" ")){
            
           
                String command = message;
                parseCommand(command, event.getMessage());
           
            }
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
            
            int n;
            try{
                n = Integer.parseInt(array[array.length-1]);
            }
            catch(NumberFormatException e){
                n = -1;
            }
            if(n == -1)
                commands.add(new Command(actionMap.get(array[1]),array));
            else
                commands.add(new Command(actionMap.get(array[1]),array,n));
        }
                    
    }
    
    public void onFileRecieved(MessageEmbedEvent event){
        System.out.println("File Recieved Event");
    }
    
    public boolean parseCommand(String input, IMessage message){
        
        input = input.substring(4);
        String[] split = input.split(" ");
        Command loaded = null;
        ArrayList<String> args = new ArrayList<String>();
        boolean isCommand = false;
        for(String s : split){
            Command c = Command.matchCommand(commands, s);
            
            if(c!=null){
                if(loaded!=null){
                    if(canUse(loaded.getAdminLevel(),message.getAuthor(),message.getGuild())){
                        String[] array = new String[args.size()];
                        loaded.getAction().exec(args.toArray(array), message);
                        args.clear();
                        isCommand = true;
                    }
                    else{
                        sendMessage(message.getChannel(),"You do not have permission to use this command");
                    }
                }
                loaded = c;
            }
            else{
                args.add(s);
            }
        }
        if(loaded!=null){
            if(canUse(loaded.getAdminLevel(),message.getAuthor(),message.getGuild())){
                String[] array = new String[args.size()];
                loaded.getAction().exec(args.toArray(array), message);
                args.clear();
                isCommand = true;
            }
            else{
                sendMessage(message.getChannel(),"You do not have permission to use this command");
            }
        }
       

        return isCommand;
    }
    
    public void addModule(Module module){
        ArrayList<Command> commands = module.getCommands();
        try {
            module.getClass().getMethod("onMessage",MessageReceivedEvent.class);
            if(this.onMessageOverride!=null){
                System.out.println("Module "+module+" attempted to override onMessage() however module "+onMessageOverride+" has already overrided this, continuing without replacing");
            }
            else{
                onMessageOverride = module;
                System.out.println("Module "+module+" successfully overrided onMessage()");
            }
            
        } catch (NoSuchMethodException ex) {
            
        } catch (SecurityException ex) {
            Logger.getLogger(BotServer.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        int commAdded = 0;
        int commRejected = 0;
        module.setServer(this);
        modules.add(module);
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
 
    public boolean canUse(int adminLevel, IUser user, IGuild guild){
       
        if(adminLevel == 0){
            return true;
        }
        
        else if(adminLevel >= 1){
            return isAdmin(user, guild);
        }
        
        else{
            return false;
        }
        
    }
    public boolean isAdmin(IUser user, IGuild guild){
         
        
        List<IRole> roles = guild.getRolesForUser(user);
        //System.out.println("Admin :"+Permissions.ADMINISTRATOR.ordinal());
        for(IRole role: roles){
            
            System.out.println(role.getName());
            for(Permissions p : role.getPermissions()){
               if(p.hasPermission(Permissions.ADMINISTRATOR.ordinal())){
                   return true;
               }
            }
        }
        return false;
    }
}
