/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.wilsonbot.Server;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author User
 */
public class TestInterface extends Server{
    
    
    
    public static void sendMessage(IChannel channel, String message){
        System.out.println(channel.getName()+" :"+message);
    }
    
    
    public void onMessage(String message){
        
        IMessage mess = new IMessage();
        
        String[] split = message.split(" ");
       
        parseCommand(split[0], );
        
       
    }
    
}
