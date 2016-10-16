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
 * @author Reetoo
 */
public abstract class WilsonModule implements IModule {

    public WilsonModule(WilsonServer server){
        
    }
    
    @Override
    public abstract boolean enable(IDiscordClient idc);

    @Override
    public abstract void disable();

    @Override
    public abstract String getName();

    @Override
    public abstract String getAuthor();

    @Override
    public abstract String getVersion();

    @Override
    public abstract String getMinimumDiscord4JVersion();
    
    
    
}
