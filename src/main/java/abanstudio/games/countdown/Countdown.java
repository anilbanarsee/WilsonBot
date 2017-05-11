/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.countdown;

import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author General
 */
public class Countdown extends Game {

    public Countdown(BotServer server){
        super(server);
    }
    
    @Override
    public void startgame(IMessage message) {
        IChannel channel = message.getChannel();
        server.sendMessage(channel, "Countdown game starting.");
        run();
    }

    @Override
    public void endGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        while(true){
            
        }
    }

    @Override
    public int getJoinRule() {
        return Game.JOINRULE_OPEN;
    }

    @Override
    public int getStartAction() {
        return Game.STARTACTION_MOVEALL_CREATOR;
    }
    
}
