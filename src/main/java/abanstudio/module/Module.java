/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.discordbot.BotServer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Reetoo
 */
public abstract class Module {
    
   protected BotServer server;
   protected String[][] commData;
   protected ArrayList<Command> commands;
   protected HashMap<String, Action> actionMap;    
   
   public Module(){
       initalizeCommands();
   }
   protected abstract void initalizeActions();
   protected abstract void initalizeCommData();
   public abstract void onReady();
   public abstract String getName();
   protected final void initalizeCommands(){
        
        initalizeActions();
        initalizeCommData();
        
        commands = new ArrayList<>();
        for(String[] array : commData){
            
            commands.add(new Command(actionMap.get(array[1]),array));
                
        }
                    
    }
   public void setServer(BotServer server){
       this.server = server;
   }
   public ArrayList<Command> getCommands(){
       return commands;
   }
}

