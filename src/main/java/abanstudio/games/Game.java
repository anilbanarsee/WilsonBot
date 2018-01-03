/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games;

import abanstudio.discordbot.BotServer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

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
    protected IChannel channel;
    protected boolean started;

   //Abstract constructor for game objects. Settings and message must be within the parameters of the message
   //due to the nature of the reflection carried out later.
    public Game(BotServer server, IChannel channel){
        this.server = server;
        this.channel = channel;
        started = false;
    }
    
    public abstract void startGame(List<IUser> players);
    public abstract void endGame();
    @Override
    public abstract void run();
    public abstract int getJoinRule();
    public abstract int getStartAction();
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract void playerJoined();
    public abstract void message(IMessage message);

    public boolean hasStarted(){
        return started;
    }
}
