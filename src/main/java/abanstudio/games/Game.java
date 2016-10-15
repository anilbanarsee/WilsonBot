/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games;

import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Reetoo
 */
public interface Game extends Runnable{

   
    
    
    public void startgame(IMessage message);
    public void endGame();
    public void printScores();
    public void nextRound();
    public void changeSetting(int i[][]);

    
}
