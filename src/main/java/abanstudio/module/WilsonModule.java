/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 *
 * @author General
 */
public abstract class WilsonModule implements IModule{

    @Override
    public boolean enable(IDiscordClient idc) {
        return true;
    }
    
    public boolean setCommands(){
        
    }
    
    @Override
    public void disable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAuthor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
