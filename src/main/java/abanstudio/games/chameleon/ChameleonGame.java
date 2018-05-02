/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.chameleon;

import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * @author Reetoo
 */
public class ChameleonGame extends Game
{

	String[][] words;
	ArrayList<IUser> players;
	IUser currentSpy;

	boolean state_pre, state_main, state_end;

	boolean running;

	public ChameleonGame(BotServer server, IChannel channel)
	{
		super(server, channel);
		words = new String[4][4];
		running = true;
		state_pre = true;
		state_main = false;
		state_end = false;
	}

	@Override
	public void startGame(List<IUser> players)
	{
		//server.sendMessage(message.getChannel(), "game starting");
		generateWords();
		server.sendMessage(channel, "Welcome to Chameleon! \nThe aim of this game is to either evade detection, if you are a spy, or to detect the spy, if you are not.\n Each round you will be presented with an array of words all relating to a theme.\n One word among these will be selected and revealed to every non-spy. \n Each player will then be asked to give a single word which relates to the selected word. \n After a brief debating period one player will be selected as the accused spy.\nIf the spy remained undetected, the spy gains 2 points. If the spy is detected, all non-spies gain 2 points, unless the spy has correctly named the selected word, in which case, the spy gains 1 point (non-spies gain nothing).\nThe first player to 7 points wins. Good Luck!");

	}

	@Override
	public void endGame()
	{

	}

	@Override
	public void run()
	{
		int pre = 0;
		int main = 0;
		int end = 0;
		while (running) {
			while (state_pre) {
				System.out.println("pre" + pre);
				pre++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (state_main) {
				System.out.println("main" + main);
				main++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			while (state_end) {
				System.out.println("end" + end);
				end++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
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
		return null;
	}

	@Override
	public int getStartAction()
	{

		return Game.STARTACTION_NONE;
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
		if (message.getContent().equals("pre")) {
			state_pre = true;
			state_main = false;
			state_end = false;
		} else if (message.getContent().equals("main")) {
			state_main = true;
			state_pre = false;
			state_end = false;
		} else if (message.getContent().equals("end")) {
			state_end = true;
			state_pre = false;
			state_main = false;
		}
	}

	public void generateWords()
	{
		String[] list = {"Indiana Jones", "Popeye", "Spiderman", "Darth Vader", "Sherlock Holmes", "Gandalf the Grey", "Superman", "Batman", "James Bond", "Dracula", "Homer Simpson", "Frankenstein", "Robin Hood", "Mario", "Tarzan", "Hercules"};
		int x = 0;
		for (String[] row : words) {
			for (int i = 0; i < row.length; i++) {
				row[i] = list[x];
				x++;
			}
			System.out.println(Arrays.toString(row));
		}

	}

}
