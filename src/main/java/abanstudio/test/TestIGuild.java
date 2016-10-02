/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.handle.obj.IRegion;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author User
 */
public class TestIGuild implements IGuild{
    
    String id;
    List<IUser> users;
    List<IChannel> channels;
    
    public TestIGuild(String id){
        this.id = id;
    }

    @Override
    public String getOwnerID() {
        return null;
    }

    @Override
    public IUser getOwner() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getIconURL() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IChannel> getChannels() {
        return channels;
    }

    @Override
    public IChannel getChannelByID(String string) {
        throw new UnsupportedOperationException("ERROR GET CHANNEL BY ID"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IUser> getUsers() {
        return users;
    }

    @Override
    public IUser getUserByID(String string) {
        throw new UnsupportedOperationException("ERROR GET USER BY ID"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IRole> getRoles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IRole getRoleByID(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IVoiceChannel> getVoiceChannels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IVoiceChannel getVoiceChannelByID(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IVoiceChannel getAFKChannel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAFKTimeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IRole createRole() throws MissingPermissionsException, HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IUser> getBannedUsers() throws HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void banUser(IUser iuser) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void banUser(IUser iuser, int i) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pardonUser(String string) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void kickUser(IUser iuser) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void editUserRoles(IUser iuser, IRole[] iroles) throws MissingPermissionsException, HTTP429Exception, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeName(String string) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeRegion(IRegion ir) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeIcon(Optional<Image> optnl) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeAFKChannel(Optional<IVoiceChannel> optnl) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeAFKTimeout(int i) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteGuild() throws DiscordException, HTTP429Exception, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void leaveGuild() throws DiscordException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IChannel createChannel(String string) throws DiscordException, MissingPermissionsException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IVoiceChannel createVoiceChannel(String string) throws DiscordException, MissingPermissionsException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IRegion getRegion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transferOwnership(IUser iuser) throws HTTP429Exception, MissingPermissionsException, DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IRole getEveryoneRole() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IInvite> getInvites() throws DiscordException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reorderRoles(IRole... iroles) throws DiscordException, HTTP429Exception, MissingPermissionsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getUsersToBePruned(int i) throws DiscordException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int pruneUsers(int i) throws DiscordException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addBot(String string, Optional<EnumSet<Permissions>> optnl) throws MissingPermissionsException, DiscordException, HTTP429Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AudioChannel getAudioChannel() throws DiscordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IDiscordClient getClient() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IGuild copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
