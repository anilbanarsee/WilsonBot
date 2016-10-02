/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author User
 */
public class TestIMessage implements IMessage{

    private String message;
    
    public TestIMessage(String s){
        message = s;
    }
    
    @Override
    public String getContent() {
        return message;
    }

    @Override
    public IChannel getChannel() {
       
    }

    @Override
    public IUser getAuthor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalDateTime getTimestamp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IUser> getMentions() {
        return null;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }

    @Override
    public void reply(String string) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        
    }

    @Override
    public IMessage edit(String string) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        return null;
    }

    @Override
    public boolean mentionsEveryone() {
        return false;
    }

    @Override
    public void delete() throws MissingPermissionsException, HTTP429Exception, DiscordException {
  
    }

    @Override
    public Optional<LocalDateTime> getEditedTimestamp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IGuild getGuild() {
        
    }

    @Override
    public String getID() {
        return "123";
    }

    @Override
    public IDiscordClient getClient() {
        return null;
    }

    @Override
    public IMessage copy() {
        return null;
    }
    
}
