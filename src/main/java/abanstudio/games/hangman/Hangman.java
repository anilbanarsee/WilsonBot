/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.hangman;

import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

/**
 * @author General
 */
public class Hangman extends Game
{


	public Hangman(BotServer server, IChannel channel)
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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getJoinRule()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public int getStartAction()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
