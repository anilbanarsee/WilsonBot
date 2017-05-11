/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.hangman;

import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;
import abanstudio.module.Games;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author General
 */
public class Hangman extends Game{
    
    Games gmodule;
    
    public Hangman(BotServer server, Games g){
        super(server);
        gmodule = g;
    }
    
    @Override
    public void startgame(IMessage message) {
        IChannel channel = message.getChannel();
        server.sendMessage(channel, "Hangman game starting");
        run();
    }

    @Override
    public void endGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getJoinRule() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStartAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
