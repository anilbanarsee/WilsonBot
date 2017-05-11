/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.countdown;

import abanstudio.games.Game;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author General
 */
public class Countdown implements Game {

    @Override
    public void startgame(IMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return Game.JOINRULE_OPEN;
    }

    @Override
    public int getStartAction() {
        return Game.STARTACTION_MOVEALL_CREATOR;
    }
    
}
