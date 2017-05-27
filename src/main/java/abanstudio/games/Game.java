/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games;

import abanstudio.discordbot.BotServer;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Reetoo
 */
public abstract class Game implements Runnable{
    public static final int JOINRULE_CHANNEL = 0;
    public static final int JOINRULE_OPEN = 1;
    public static final int JOINRULE_CREATOR = 2;
    public static final int JOINRULE_NONE = 3;
    public static final int STARTACTION_MOVEALL = 0;
    public static final int STARTACTION_MOVEALL_NEW = 1;
    public static final int STARTACTION_MOVEALL_FAIR = 2;
    public static final int STARTACTION_MOVEALL_CREATOR = 3;
    public static final int STARTACTION_NONE = 4;
    protected final BotServer server;

   
    public Game(BotServer server ){
        this.server = server;
    }
    
    public abstract void startgame(IMessage message);
    public abstract void endGame();
    @Override
    public abstract void run();
    public abstract int getJoinRule();
    public abstract int getStartAction();    

}
