/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.utils.sqlite.DBHandler;
import java.util.HashMap;
import java.util.List;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;


/**
 *
 * @author General
 */
public class Admin extends Module{

    protected HashMap<String, IChannel> timeChanMap;
    protected HashMap<String, IRole> timeRoleMap;
    protected String defTimeoutName = "Timeout";
    protected String defTimeoutRole = "Timeout";
    
    @Override
    protected void initalizeActions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void initalizeCommData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void onReady(){
        
        initTimeoutChannels();
        
    }
    
    public void initTimeoutRole(String[] args, IMessage message){
        
        IGuild g = message.getGuild();
        
        if(args.length==0)
            server.sendMessage(message.getChannel(), "You need to give a voicechannel name");
        
        boolean flag = false;
        for(IChannel chan: g.getVoiceChannelsByName(args[0])){
            timeChanMap.put(g.getID(), chan);
            DBHandler.setGuildSetting(g.getID(), "timeout_chan", args[0]);
            flag = true;
            break;
        }
        if(!flag){
            server.sendMessage(message.getChannel(), "Could not find a channel with that name");
        }
        
    }
    public void setTimeoutRole(IGuild guild){
        
    }
    
    protected void initTimeoutChannels(){
        timeChanMap = new HashMap<>();
        List<IGuild> guilds = server.client.getGuilds();
        for(IGuild guild : guilds){
            setTimeoutChannel(guild);
        }
    }
    protected void setTimeoutChannel(IGuild guild){
        
        List<IChannel> channels = guild.getChannels();
        boolean set = false;
        
        String name = DBHandler.getGuildSetting(guild.getID(), "timeout_chan");
        if(name.equals("null")){
            name = defTimeoutName;
        }
        
        for(IChannel channel: channels){
            
            
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
    public void timeout(String[] args, IMessage message){
        
        IChannel chan = message.getChannel();
        IChannel tOChan = timeChanMap.get(message.getGuild().getID());
        if(args.length==0){
            server.sendMessage(chan, "You haven't given a user ID");
        }
        
        for(IUser user : message.getGuild().getUsers()){
            if(user.getID()==args[0]){
                //user.addRole(irole);
            }
        }
        
    }
    

    
}
