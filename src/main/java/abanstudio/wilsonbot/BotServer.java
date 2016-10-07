/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import abanstudio.command.Command;
import static abanstudio.wilsonbot.WilsonServer.matches;
import java.util.Arrays;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author User
 */
abstract public class BotServer{
    
    
    Command[] commands;
    String[][] commMap;
    
    
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
        
        for(x = 0; x<commMap.length; x++){
            String comm = commMap[x][1];
            if(command.equals(comm)){
                commands[x].exec(newarg, message);
            }
        }
        
    }
    
}
