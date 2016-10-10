/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.command;

import abanstudio.wilson.BotServer;
import abanstudio.wilsonbot.WilsonServer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Reetoo
 */
public interface Command {
    
    
    
    public void exec(String[] arguments, IMessage message);
    
}
