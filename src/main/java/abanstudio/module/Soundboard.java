/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.discordbot.BotServer;
import java.util.HashMap;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Reetoo
 */
public class Soundboard extends Module{
    
    String[][] comms = {{}};
        
    public Soundboard(BotServer server) {
        super(server);
        this.initalizeCommands();
        server.addModule(this);
    }

    @Override
    protected void initalizeActions() {
        actionMap = new HashMap<>();
        
        actionMap.put("ping",  new Action(){public void exec(String[] arg, IMessage m) {ping(arg,m);}});    
    }

    @Override
    protected void initalizeCommData() {
        commData = comms;
    }
    
    public void ping(String[] arg, IMessage m){
        server.sendMessage(m.getChannel(), "pong");
    }
}
