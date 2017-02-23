/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import java.util.HashMap;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author General
 */
public class Games extends Module{

    String[][] comms = {{}};
    
    @Override
    protected void initalizeActions() {
        actionMap = new HashMap<>();
        
        actionMap.put("ping",  new Action(){public void exec(String[] arg, IMessage m) {ping(arg,m);}});    
    }

    @Override
    protected void initalizeCommData() {
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
