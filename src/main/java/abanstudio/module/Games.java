/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import java.util.HashMap;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.api.events.Event;

/**
 *
 * @author General
 */
public class Games extends Module{


    
    @Override
    protected void initalizeActions() {
        actionMap = new HashMap<>();
        
        actionMap.put("ping",  new Action(){public void exec(String[] arg, IMessage m) {ping(arg,m);}});    
    }
    
    public void onMessage(MessageReceivedEvent event){
        
         server.sendMessage(event.getMessage().getChannel(), "pong");
        
    }
    @EventSubscriber
    public void testEvent(Event event){
        System.out.println("Event received");
        System.out.println(event.getClass().getName());
    }
    
    @Override
    protected void initalizeCommData() {
            String[][] comms = {};
        commData = comms;
    }

    @Override
    public String getName() {
        return "Games";
    }
    
    public void onReady(){
        
    }
    public void ping(String[] args, IMessage m){
        
    }


}
