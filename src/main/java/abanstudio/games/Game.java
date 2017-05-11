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
    static final int JOINRULE_CHANNEL = 0;
    static final int JOINRULE_OPEN = 1;
    static final int JOINRULE_CREATOR = 2;
    static final int JOINRULE_NONE = 3;
    static final int STARTACTION_MOVEALL = 0;
    static final int STARTACTION_MOVEALL_NEW = 1;
    static final int STARTACTION_MOVEALL_FAIR = 2;
    static final int STARTACTION_MOVEALL_CREATOR = 3;
    static final int STARTACTION_NONE = 4;
   

   
    
    
    public void startgame(IMessage message);
    public void endGame();
    @Override
    public void run();
    public int getJoinRule();
    public int getStartAction();
     
    
}
