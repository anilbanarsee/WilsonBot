/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot.wilson;

import abanstudio.command.CommandData;
import abanstudio.discordbot.BotServer;
import abanstudio.command.Action;
import abanstudio.utils.sqlite.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.audio.AudioPlayer;


/**
 * @author Reetoo
 */
public class WilsonServer extends BotServer
{


	//ArrayList<ArrayList<Thread>> gameThread;


	ArrayList<IUser> parlayUsers;
	ArrayList<CommandData> commData;

	public WilsonServer(IDiscordClient client)
	{

		super(client);
		prefix = "dog";

		parlayUsers = new ArrayList<>();

	}



	@Override
	public void onServerReady(ReadyEvent event)
	{
		System.out.println("WilsonBot initalizing");

	}


	@Override
	protected HashMap<String, Action> initalizeActions()
	{
		HashMap<String, Action> actionMap = new HashMap<>();

		actionMap.put("join", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				join(arg, m);
			}
		});
		actionMap.put("parlay", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				parlay(m);
			}
		});
		actionMap.put("unparlay", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				unparlay(m);
			}
		});
		actionMap.put("list", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				list(arg, m);
			}
		});
		actionMap.put("list:test",  new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				list(arg, m);
			}
		});
		// actionMap.put("set",new Action(){public void exec(String[] arg, IMessage m) {set(arg,m);}});
		actionMap.put("shutdown", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				shutdown(m);
			}
		});
		actionMap.put("skip", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				skip(arg, m);
			}
		});

		return actionMap;
	}

	@Override
	protected List<CommandData> initalizeCommData()
	{
		ArrayList<CommandData> comms = new ArrayList<>();
		commData = comms;
		comms.add(new CommandData("[lL]ist", "list", "test description"));
		comms.add(new CommandData( "[jJ]oin", "join", "test description"));
		return comms;
	}

	@Override
	protected void initalizeEventMethods()
	{

	}


	private void shutdown(IMessage message)
	{
		if (!isMasterAdmin(message.getAuthor())) {
			sendMessage(message.getChannel(), "You must be a master-admin to invoke this command. (This command completely shutsdown the bot, would require sshing back in to reset it.");
			return;
		}
		sendMessage(message.getChannel(), "Bot shutting down");
		System.exit(0);
	}


	public void parlay(IMessage message)
	{
		IUser user = message.getAuthor();
		for (IUser u : parlayUsers) {
			if (u.getStringID().equals(user.getStringID())) {
				sendMessage(message.getChannel(), "we already talkin'");
				return;
			}
		}
		sendMessage(message.getChannel(), "I'll know you are talking to me now");
		parlayUsers.add(user);

	}

	public void unparlay(IMessage message)
	{
		IUser user = message.getAuthor();
		for (int i = 0; i < parlayUsers.size(); i++) {
			if (parlayUsers.get(i).getStringID().equals(user.getStringID())) {
				sendMessage(message.getChannel(), "Catch you later.");
				parlayUsers.remove(i);
				return;
			}
		}
		sendMessage(message.getChannel(), "You never had a parlay with me");
	}


	private void list(String[] arguments, IMessage message)
	{

		String s = "";
		if (arguments.length == 0) {

			sendMessage(message.getChannel(), "Here are my commands:");

			for (CommandData comm : commData) {
				s += "_" + comm.getComm() + "_" + " : `" + comm.getDescS() + "`\n";
				s += "\n";
			}

			sendMessage(message.getChannel(), s);

			return;
		}

	}

	public void skip(String[] args, IMessage message)
	{
		//UserLog ul = userLogs.get(message.getAuthor().getID());
		IGuild guild = message.getGuild();
		AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
		String returnMessage = "";
		if (args.length != 0) {
			if (args[0].equals("skip")) {
				player.clear();
				returnMessage = message.getAuthor() + " skipped all queued clips";
			}
		} else {
			player.skip();
			returnMessage = message.getAuthor().getName() + " skipped the current clip.";
		}

		sendMessage(message.getChannel(), returnMessage);
	}


	public boolean isMasterAdmin(IUser user)
	{
		return DBHandler.getAdminRights(user.getStringID()).equals("master");
	}

	@Override
	public boolean canUse(int adminLevel, IUser user, IGuild guild)
	{

		switch (adminLevel) {
			case 0:
				return true;
			case 1:
				return isAdmin(user, guild) || isMasterAdmin(user);
			case 2:
				return isMasterAdmin(user);
			default:
				return false;
		}

	}


}
