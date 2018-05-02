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
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

/**
 * @author General
 */
public class Countdown extends Game
{

	public Countdown(BotServer server, IChannel channel)
	{
		super(server, channel);
	}

	@Override
	public void startGame(List<IUser> players)
	{

	}

	@Override
	public void endGame()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void run()
	{
		while (true) {

		}
	}

	@Override
	public int getJoinRule()
	{
		return Game.JOINRULE_OPEN;
	}

	@Override
	public String getName()
	{
		return "Countdown";
	}

	@Override
	public int getStartAction()
	{
		return Game.STARTACTION_MOVEALL_CREATOR;
	}

	@Override
	public int getMinPlayers()
	{
		return 0;
	}

	@Override
	public int getMaxPlayers()
	{
		return 0;
	}

	@Override
	public void playerJoined(IUser user)
	{

	}

	@Override
	public void message(IMessage message)
	{


	}

}
