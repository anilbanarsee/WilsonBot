/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.utils.sqlite.DBHandler;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


/**
 *
 * @author General
 */
public class Admin extends Module{

    protected HashMap<String, IVoiceChannel> timeChanMap;
    protected HashMap<String, IRole> timeRoleMap;
    protected String defTimeoutName = "Timeout";
    protected String defTimeoutRole = "Timeout";
    protected int timeoutTime = 15;
    
   
    public Admin(){
        super();
        timeRoleMap = new HashMap<>(); 
    }
    
    @Override
    protected void initalizeActions() {
        actionMap = new HashMap<>();
        
        actionMap.put("timeout",  new Action(){@Override
        public void exec(String[] arg, IMessage m) {timeout(arg,m);}});        }

    @Override
    protected void initalizeCommData() {
        String[][] comms = {{"[tT]imeout","timeout","timeout user","'dog timeout [userid]' to timeout user"}};
        commData = comms;
        System.out.println("Hello");
    }

    @Override
    public String getName() {
        return "Admin";
    }
    
    @Override
    public void onReady(){
        
        initTimeoutChannels();
        initTimeoutRoles();
        
    }
    
    public void setTimeoutChannel(String[] args, IMessage message){
        
        IGuild g = message.getGuild();
        
        if(args.length==0)
            server.sendMessage(message.getChannel(), "You need to give a voicechannel id");
        
        boolean flag = false;
        IVoiceChannel chan = message.getGuild().getVoiceChannelByID(args[0]);
        if(chan == null){
            server.sendMessage(message.getChannel(), "Could not find a channel with that id on this server");
            return;
     
        }
        timeChanMap.put(g.getID(), chan);
        DBHandler.setGuildSetting(g.getID(), "timeout_chan", args[0]);

        
    }
    public void setTimeoutRole(String[] args, IMessage message){
        IGuild g = message.getGuild();
        
        if(args.length==0)
            server.sendMessage(message.getChannel(), "You need to give a role id");
        
        boolean flag = false;
        IRole role = message.getGuild().getRoleByID(args[0]);
        if(role == null){
             server.sendMessage(message.getChannel(), "Could not find a role with that name");
             return;
        }
        timeRoleMap.put(g.getID(), role);
        DBHandler.setGuildSetting(g.getID(), "timeout_role", args[0]);
        flag = true;
           

    }  
    protected void initTimeoutChannels(){
        timeChanMap = new HashMap<>();
        List<IGuild> guilds = server.client.getGuilds();
        for(IGuild guild : guilds){
            initTimeoutChannel(guild);
        }
    }
    protected void initTimeoutChannel(IGuild guild){
        
        List<IVoiceChannel> channels = guild.getVoiceChannels();
        boolean set = false;
        
        String name = DBHandler.getGuildSetting(guild.getID(), "timeout_chan");
        if(name.equals("null")){
            name = defTimeoutName;
        }
        
        for(IVoiceChannel channel: channels){
            
            
            if(channel.getName().equals(name)){
                set = true;
                timeChanMap.put(guild.getID(), channel);
                return;
            }
        }
        
        if(!set){
            timeChanMap.put(guild.getID(), null);
        }
            
        
        
        
    }
    protected void initTimeoutRoles(){
        timeRoleMap = new HashMap<>();
        List<IGuild> guilds = server.client.getGuilds();
        for(IGuild guild : guilds){
            initTimeoutRole(guild);
        }
    }
    protected void initTimeoutRole(IGuild guild){
        
        List<IRole> roles = guild.getRoles();
        boolean set = false;
        
        String name = DBHandler.getGuildSetting(guild.getID(), "timeout_role");
        if(name.equals("null")){
            name = defTimeoutRole;
        }
        
        for(IRole role: roles){
            
            
            if(role.getName().equals(name)){
                set = true;
                timeRoleMap.put(guild.getID(), role);
                return;
            }
        }
        
        if(!set){
            timeRoleMap.put(guild.getID(), null);
        }
            
        
        
        
    }
    public void timeout(String[] args, IMessage message){
        
        IChannel chan = message.getChannel();
        IGuild guild = message.getGuild();
        if(args.length==0){
            server.sendMessage(chan, "You haven't given a user ID");
        }
        
        for(IUser user : guild.getUsers()){
            if(user.getID().equals(args[0])){
                
                IVoiceChannel temp = null;
                for(IVoiceChannel ch: guild.getVoiceChannels()){
                    if(ch.getUsersHere().contains(user)){
                        temp = ch;
                    }
                }
                IVoiceChannel origin = temp;
                try {
                    user.addRole(timeRoleMap.get(guild.getID()));
                    user.moveToVoiceChannel(timeChanMap.get(guild.getID()));
                } catch (MissingPermissionsException ex) {
                    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RateLimitException ex) {
                    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DiscordException ex) {
                    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                }
                Thread t = new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(timeoutTime);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        user.removeRole(timeRoleMap.get(guild.getID()));
                        user.moveToVoiceChannel(origin);
                    } catch (DiscordException ex) {
                        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RateLimitException ex) {
                        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MissingPermissionsException ex) {
                        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                t.start();
            }
        }
        
    }
    

    
}
