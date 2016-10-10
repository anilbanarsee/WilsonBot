/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilson;

import abanstudio.wilsonbot.*;
import abanstudio.command.Command;
import abanstudio.exceptions.InvalidSearchException;
import static abanstudio.wilsonbot.WilsonServer.matches;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageEmbedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
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
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author User
 */
abstract public class BotServer{
    
    
    protected Command[] commands;
    protected String[][] commMap;
    protected IDiscordClient client;
    protected ArrayList<IRole> roles;
    
    public String prefix = "dog";
    
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
            if((message.startsWith(prefix+" ")))
                command = message.substring(4);
            parseCommand(command, event.getMessage());
           
        }
        
            
        
    }
    
    @EventSubscriber
    public void onDisconnect(DiscordDisconnectedEvent event) throws DiscordException{
        System.out.println("Bot disconnected with reason "+event.getReason()+". Reconnecting...");
        Main.main(null);
    }
    
    public void onFileRecieved(MessageEmbedEvent event){
        System.out.println("File Recieved Event");
    }
    
    protected String matchCommand(String command) {
       

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
       
       String[] arguments = arg.split("\\s+");
       int length = arguments.length;
       for(String s : arguments){
           s = s.replace("\\s+", "");
           if(s.equals("")) length--;
       }
       int x = 0;
       String[] argument = new String[length];
       for(String s: arguments){
           if(!s.equals("")){
               argument[x] = s;
               x++;
           }
       }
       for(int i=0; i<split.length; i++){
           String test = matchCommand(split[i]);
           
           if(!test.equals("null")){
               doCommand(com,argument,message);
               com = test;
               arg = "";
           }
           else{
               arg += " "+split[i];
           }
           
           
       }
       
       doCommand(com,argument,message);
       
    }
    
    public void doCommand(String command, String[] argument, IMessage message){
       
        
        System.out.println(Arrays.toString(argument));
        
        for(int i = 0; i<commMap.length; i++){
            String comm = commMap[i][1];
            if(command.equals(comm)){
                commands[i].exec(argument, message);
            }
        }
        
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
    
    public boolean isAdmin(IUser user, IGuild guild){
        List<IRole> roles = user.getRolesForGuild(guild);
        for(IRole role : roles){
           
            if(role.getPermissions().contains(Permissions.ADMINISTRATOR)){
                return true;
            }
            //Permissions p = (Permissions) role.getPermissions().toArray()[0];
            //if(p.hasPermission(Permissions.ADMINISTRATOR)){return true;}
           
        }
        return false;
    }
}
