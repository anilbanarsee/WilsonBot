/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games;

import abanstudio.discordbot.BotServer;
import abanstudio.utils.sqlite.DBHandler;

import java.io.File;

import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * @author Reetoo
 */
public class PokemonGuessGame extends Game
{


	private volatile boolean running = true;
	private volatile boolean guessing = true;
	IChannel channel;
	private List<ArrayList<Object>> list = null;
	private String currentPokemon;

	public PokemonGuessGame(BotServer server, IChannel channel, String[] settings)
	{
		super(server, channel);
		int gen = Integer.parseInt(settings[0]);
		boolean b = false;
		String s = "";
		if (settings[1].equals("t")) {
			b = true;
		}
		if (b) {
			list = DBHandler.pokemonIncGen(gen);
			s = "up to";
		} else {
			list = DBHandler.pokemonAtGen(gen);
			s = "at";
		}
		server.sendMessage(this.channel, "Pokemon guess game set-up, using pokemon " + s + " gen " + gen + ",");

	}


	@Override
	public void startGame(List<IUser> players)
	{
		server.sendMessage(channel, "Starting game");
		run();
	}

	@Override
	public void endGame()
	{
		System.out.println("ending");
		running = false;
	}


	public void guessPokemon(String s)
	{
		if (s.equals(currentPokemon)) {
			guessing = false;

			server.sendMessage(channel, "You guessed " + currentPokemon + " correctly!");

		} else {
			server.sendMessage(channel, s + " was incorrect");
		}
	}

	@Override
	public void run()
	{
		int i = 0;
		while (running) {
			guessing = true;
			Random r = new Random();
			int randomInt = r.nextInt(list.size()) + 1;
			currentPokemon = (String) list.get(randomInt).get(0);
			System.out.println(currentPokemon);
			int pokeNum = (Integer) list.get(randomInt).get(1);
			File f = new File("assets/pokemon/sil/" + pokeNum + ".png");
			File f2 = new File("assets/pokemon/artwork/" + pokeNum + ".png");

			BotServer.sendFile(channel, f);

			while (guessing && running) {
				try {
					sleep(50);
				} catch (InterruptedException ex) {
					Logger.getLogger(PokemonGuessGame.class.getName()).log(Level.SEVERE, null, ex);
				}
			}


		}
	}

	public void print(String text)
	{
		if (server == null) {
			System.out.println(text);
		} else {
			server.sendMessage(channel, text);
		}
	}

	@Override
	public int getJoinRule()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getName()
	{
		return "PokemonGuessGame";
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
