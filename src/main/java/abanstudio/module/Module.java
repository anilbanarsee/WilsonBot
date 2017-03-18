/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.command.CoreAction;
import abanstudio.discordbot.BotServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

/**
 *
 * @author Reetoo
 */
public abstract class Module {
    
   protected BotServer server;
   protected IDiscordClient client;
   protected String[][] commData;
   protected ArrayList<Command> commands;
   //protected ArrayList<Command> overrideCommands;
   protected HashMap<String, Action> actionMap;
   protected HashMap<String, Action> overrides;
   
   
   public Module(BotServer server){
       setServer(server);
       initalizeCommands();
       overrides = new HashMap<>();
       
   }
   protected abstract void initalizeActions();
   protected abstract void initalizeCommData();
   public abstract void onReady();
   public abstract String getName();
   protected final void initalizeCommands(){
         initalizeActions();
        initalizeCommData();
        
        commands = new ArrayList<>();
        
        for(Entry<String, Action> entry : actionMap.entrySet()){
            
            String[] cData = {};
            boolean flag = false;
            
            for(String[] array : commData){
                if(array[1].equals(entry.getKey())){
                    flag = true;
                    cData = array;
                }
            }
            if(flag){
                int n = -345;
                boolean parsed = false;
                try{
                    n = Integer.parseInt(cData[cData.length-1]);
                    parsed = true;
                }
                catch(NumberFormatException e){
                    commands.add(new Command(actionMap.get(cData[1]),cData));
                    
                }
                if(parsed)
                    commands.add(new Command(actionMap.get(cData[1]),cData,n));
                
            
            }
        }
        

                    
    }
   public HashMap<String, Action> getOverrides(){return overrides;}
   private void setServer(BotServer server){
       this.server = server;
       client = server.client;
   }
   public ArrayList<Command> getCommands(){
       return commands;
   }
   public boolean overridesMethods(){
       return overrides.size()>0;
   }
   @Override
   public String toString(){
       return getName();
   }
   public void sendMessage(IChannel channel, String message){
       server.sendMessage(channel, message);
   }
   
}

