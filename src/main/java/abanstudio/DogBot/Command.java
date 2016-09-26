/*
The Anyone Can Use This Licence 
 */
package abanstudio.DogBot;

import abanstudio.DogBot.Main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author Anil Banarsee
 */
public interface Command {
    
    

    public void start(String[] arguments, IMessage message);
    
    
}
