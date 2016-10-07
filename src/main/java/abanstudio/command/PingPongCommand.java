/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.command;

import abanstudio.wilsonbot.WilsonServer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author User
 */
public class PingPongCommand extends Command{

    public PingPongCommand(WilsonServer server) {
        super(server);
    }

    @Override
    public void exec(String[] arguments, IMessage message) {
        for(String s: arguments){
            if(s.equals("ping")){
                server.sendMessage(message.getChannel(),"pong");
            }
        }
    }
    
}
