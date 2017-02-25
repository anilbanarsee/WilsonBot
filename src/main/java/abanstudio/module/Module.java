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
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 *
 * @author Reetoo
 */
public abstract class Module {
    
   protected BotServer server;
   protected String[][] commData;
   protected ArrayList<Command> commands;
   protected HashMap<String, Action> actionMap;
   protected HashMap<String, CoreAction> overrides;
   protected boolean onMessage;
   
   public Module(){
       initalizeCommands();
   }
   protected abstract void initalizeActions();
   protected abstract void initalizeCommData();
   public abstract void onReady();
   public abstract String getName();
   public abstract boolean overridesOnMessage();
   protected final void initalizeCommands(){
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
   public HashMap<String, CoreAction> getOverrides(){return overrides;}
   public void setServer(BotServer server){
       this.server = server;
   }
   public ArrayList<Command> getCommands(){
       return commands;
   }
   @Override
   public String toString(){
       return getName();
   }
}

