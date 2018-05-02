/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games;

import abanstudio.discordbot.BotServer;

import java.util.List;
import java.util.Random;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * @author General
 */
public class RussianRoulette extends Game
{

	int chambers;

	public RussianRoulette(BotServer server, IChannel channel)
	{
		super(server, channel);
		chambers = 6;
	}

	public RussianRoulette(BotServer server, IChannel channel, int chambers)
	{
		this(server, channel);
		this.chambers = chambers;
	}

	@Override
	public void startGame(List<IUser> players)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void endGame()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void run()
	{


	}

	public boolean shoot()
	{
		System.out.println("Shooting, with " + chambers + " chambers remaining");
		Random r = new Random();
		int shot = r.nextInt(chambers);
		chambers--;
		return shot == 0;
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
