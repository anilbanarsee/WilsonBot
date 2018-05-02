/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.CommandData;
import abanstudio.discordbot.BotServer;
import abanstudio.discordbot.Replaces;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.utils.sqlite.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


/**
 * @author General
 */
public class Admin extends Module
{

	protected HashMap<String, IVoiceChannel> timeChanMap;
	protected HashMap<String, IRole> timeRoleMap;
	protected String defTimeoutName = "Timeout";
	protected String defTimeoutRole = "Timeout";
	protected String defCommChanName = "botcommands";
	protected HashMap<String, IChannel> commChanMap;
	protected int timeoutTime = 15;


	public Admin(BotServer server)
	{
		super(server);
		timeRoleMap = new HashMap<>();
	}

	@Override
	public HashMap<String, Action> initializeActions()
	{
		HashMap<String, Action> actionMap = new HashMap<>();


		actionMap.put("timeout", new Action()
		{
			@Override
			public void exec(String[] arg, IMessage m)
			{
				timeout(arg, m);
			}
		});
		actionMap.put("check", new Action()
		{
			@Override
			public void exec(String[] arg, IMessage m)
			{
				checkAdmin(arg, m);
			}
		});
		actionMap.put("settimeoutchannel", new Action()
		{
			@Override
			public void exec(String[] arg, IMessage m)
			{
				setTimeoutChannel(arg, m);
			}
		});
		actionMap.put("settimeoutrole", new Action()
		{
			@Override
			public void exec(String[] arg, IMessage m)
			{
				setTimeoutRole(arg, m);
			}
		});
		actionMap.put("settimeout", new Action()
		{
			@Override
			public void exec(String[] arg, IMessage m)
			{
				setTimeout(arg, m);
			}
		});
		actionMap.put("moveall", new Action()
		{
			public void exec(String[] arg, IMessage m)
			{
				moveAll(arg, m);
			}
		});


		return actionMap;
	}

	@Override
	public List<CommandData> initializeCommData()
	{
		ArrayList<CommandData> commList = new ArrayList<>();
		commList.add(new CommandData("[tT]imeout", "timeout", "timeout user").setAdminLevel(1));
		commList.add(new CommandData( "[sS]etTimeoutChannel", "settimeoutchannel", "sets the timeout channel").setAdminLevel(1));
		commList.add(new CommandData( "[sS]etTimeout", "settimeout", "sets the timeout period").setAdminLevel(1));
		commList.add(new CommandData("[sS]etTimeoutRole", "settimeoutrole", "sets the timeout role").setAdminLevel(1));
		return commList;
	}

	@Override
	public String getName()
	{
		return "Admin";
	}

	@Override
	public String getPrefix(){ return "admin"; }

	@Override
	public void onReady()
	{


		initTimeoutChannels();
		initTimeoutRoles();
		initCommChannels();

	}

	public void moveAll(String[] arguments, IMessage message)
	{
		// sendMessage(message.getChannel(), "Move all");
		if (arguments.length < 2) {
			sendMessage(message.getChannel(), "You must give two arguments dog, first is the channel origin second is the channel destination");
		}
		boolean flag = false;
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].equals("me")) {
				IVoiceChannel chan = message.getAuthor().getVoiceStateForGuild(message.getGuild()).getChannel();

				if (chan != null) {
					arguments[i] = chan.getName();
				}
			}
		}
		System.out.println(arguments[0]);
		List<IVoiceChannel> list = message.getGuild().getVoiceChannels();

		List<IUser> userList = message.getGuild().getUsers();

		IVoiceChannel target = null;
		flag = true;
		for (IVoiceChannel channel : list) {

			if (channel.getName().replace(" ", "").equals(arguments[1])) {

				target = channel;
				flag = false;
				break;


			}


		}
		if (flag) {
			sendMessage(message.getChannel(), "Could not find a channel called " + arguments[1] + ".");
			return;
		}

		if (target == null) {
			sendMessage(message.getChannel(), "That target channel does not exist playa.");
		}

		flag = false;
		for (IUser user : userList) {

			IVoiceChannel chan = user.getVoiceStateForGuild(message.getGuild()).getChannel();

			if (chan != null) {
				try {
					IVoiceChannel vchan = chan;
					if (vchan.getName().replace(" ", "").equals(arguments[0].replace(" ", "")))
						user.moveToVoiceChannel(target);
				} catch (DiscordException ex) {
					Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
				} catch (MissingPermissionsException ex) {
					Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
				} catch (RateLimitException ex) {
					Logger.getLogger(WilsonServer.class.getName()).log(Level.SEVERE, null, ex);
				}

			} else {
				flag = true;
			}
		}


	}

	public void setTimeout(String[] args, IMessage message)
	{
		if (args.length < 1) {
			server.sendMessage(message.getChannel(), "You need to give me a number");
		}

		try {
			int n = Integer.parseInt(args[0]);
			if (n > 60) {
				server.sendMessage(message.getChannel(), "Timeout cannot be longer than 60 seconds");
				return;
			}
			this.timeoutTime = n;
			server.sendMessage(message.getChannel(), "Timeout period set to " + timeoutTime);
		} catch (NumberFormatException e) {
			server.sendMessage(message.getChannel(), "That was not a number");
			return;
		}
	}

	public void setTimeoutChannel(String[] args, IMessage message)
	{

		IGuild g = message.getGuild();

		if (args.length == 0)
			server.sendMessage(message.getChannel(), "You need to give a voicechannel id");

		boolean flag = false;

		long id;
		try {
			id = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			server.sendMessage(message.getChannel(), "'" + args[0] + "' is not a number.");
			return;
		}

		IVoiceChannel chan = message.getGuild().getVoiceChannelByID(id);

		if (chan == null) {
			server.sendMessage(message.getChannel(), "Could not find a channel with that id on this server");
			return;

		}
		timeChanMap.put(g.getStringID(), chan);
		DBHandler.setGuildSetting(g.getStringID(), "timeout_chan", args[0]);
		server.sendMessage(message.getChannel(), "Timeout channel set to channel : " + chan.getName() + " (" + chan.getStringID() + ")");


	}

	public void setTimeoutRole(String[] args, IMessage message)
	{
		IGuild g = message.getGuild();

		if (args.length == 0)
			server.sendMessage(message.getChannel(), "You need to give a role id");

		boolean flag = false;
		List<IRole> roles = g.getRolesByName(args[0]);
		if (roles.size() < 1) {
			server.sendMessage(message.getChannel(), "Could not find a role with that id");
			return;
		}
		IRole role = roles.get(0);
		timeRoleMap.put(g.getStringID(), role);
		DBHandler.setGuildSetting(g.getStringID(), "timeout_role", role.getStringID());
		server.sendMessage(message.getChannel(), "Timeout role set to role : " + role.getName() + " (" + role.getStringID() + ")");

		flag = true;


	}

	protected void initTimeoutChannels()
	{
		timeChanMap = new HashMap<>();
		List<IGuild> guilds = server.client.getGuilds();
		for (IGuild guild : guilds) {
			initTimeoutChannel(guild);
		}
	}

	protected void initTimeoutChannel(IGuild guild)
	{


		String id = DBHandler.getGuildSetting(guild.getStringID(), "timeout_chan");
		if (id.equals("null")) {
			List<IVoiceChannel> chans = guild.getVoiceChannels();
			for (IVoiceChannel chan : chans) {


				if (chan.getName().equals(this.defTimeoutName)) {
					timeChanMap.put(guild.getStringID(), chan);
					return;
				}

			}

		} else {
			timeChanMap.put(guild.getStringID(), guild.getVoiceChannelByID(Long.parseLong(id)));
			return;
		}


		timeChanMap.put(guild.getStringID(), null);


	}

	protected void initTimeoutRoles()
	{
		timeRoleMap = new HashMap<>();
		List<IGuild> guilds = server.client.getGuilds();
		for (IGuild guild : guilds) {
			initTimeoutRole(guild);
		}
	}

	protected void initTimeoutRole(IGuild guild)
	{


		String id = DBHandler.getGuildSetting(guild.getStringID(), "timeout_role");
		if (id.equals("null")) {
			List<IRole> roles = guild.getRoles();
			for (IRole role : roles) {


				if (role.getName().equals(this.defTimeoutRole)) {
					timeRoleMap.put(guild.getStringID(), role);
					return;
				}

			}

		} else {
			timeRoleMap.put(guild.getStringID(), guild.getRoleByID(Long.parseLong(id)));
			return;
		}


		timeRoleMap.put(guild.getStringID(), null);


	}

	public void timeout(String[] args, IMessage message)
	{

		IChannel chan = message.getChannel();
		IGuild guild = message.getGuild();
		boolean muted = false;
		if (args.length == 0) {
			server.sendMessage(chan, "You haven't given a user ID");
		}

		for (IUser user : guild.getUsers()) {
			if (user.getStringID().equals(args[0])) {

				IVoiceChannel temp = null;
				for (IVoiceChannel ch : guild.getVoiceChannels()) {
					if (ch.getUsersHere().contains(user)) {
						temp = ch;
					}
				}
				IVoiceChannel origin = temp;
				try {
					IRole timeRole = timeRoleMap.get(guild.getStringID());
					if (timeRole != null) {
						user.addRole(timeRole);
						user.moveToVoiceChannel(timeChanMap.get(guild.getStringID()));

						muted = false;
					} else {
						guild.setMuteUser(user, true);
						muted = true;
					}

				} catch (MissingPermissionsException ex) {
					Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
				} catch (RateLimitException ex) {
					Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
				} catch (DiscordException ex) {
					Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
				}
				boolean muteFinal = muted;
				Thread t = new Thread(() ->
				{
					try {
						TimeUnit.SECONDS.sleep(timeoutTime);
					} catch (InterruptedException ex) {
						Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
					}
					try {

						if (!muteFinal) {
							user.removeRole(timeRoleMap.get(guild.getStringID()));
							user.moveToVoiceChannel(origin);


						} else {
							guild.setMuteUser(user, false);
						}
					} catch (DiscordException ex) {
						Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
					} catch (RateLimitException ex) {
						Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
					} catch (MissingPermissionsException ex) {
						Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
					}
				});
				t.start();
			}
		}

	}

	public void checkAdmin(String[] args, IMessage m)
	{
		int n = Integer.parseInt(args[0]);
		server.canUse(n, m.getAuthor(), m.getGuild());
	}

	protected void initCommChannels()
	{
		System.out.println("initalizing redirect channels");
		commChanMap = new HashMap<>();
		List<IGuild> guilds = server.client.getGuilds();
		for (IGuild guild : guilds) {
			setCommChannel(guild);
		}
	}

	protected void setCommChannel(IGuild guild)
	{

		List<IChannel> channels = guild.getChannels();
		boolean set = false;

		for (IChannel channel : channels) {


			if (channel.getName().equals(defCommChanName)) {
				commChanMap.put(guild.getStringID(), channel);
				return;
			}
		}

		if (!set) {
			commChanMap.put(guild.getStringID(), null);
		}


	}

	@EventSubscriber
	@Replaces
	public void onMessage(MessageReceivedEvent event)
	{

		IMessage m = event.getMessage();


		if (event.getMessage().getAuthor().isBot())
			return;
		String message = event.getMessage().getContent();

		if (message.startsWith(server.prefix + " ")) {


			String command = message;
			IMessage mess = event.getMessage();
			boolean isCommand = server.parseCommand(command, event.getMessage());
			if (isCommand) {
				IChannel redirect = commChanMap.get(m.getGuild().getStringID());
				if (redirect != null) {
					if (!(m.getChannel().equals(redirect))) {
						redirect(m, redirect);

					}
				}
			}
            /*else{
                ArrayList<String> links = new ArrayList<>();
                for(String s : mess.getContent().split(" ")){
                    if(true){
                        links.add(s);
                        
                    }
                }
                if(links.size()>0){
                    //server.sendMessage(mess.getAuthor().getName()+" :", null);
                }
            }*/

		}
	}

	public void redirect(IMessage m, IChannel redirection)
	{
		System.out.println("Redirecting message");
		String author = m.getAuthor().getName();
		String content = m.getContent();

		try {
			m.delete();
			server.sendMessage(redirection, "[Redirected] " + author + ": " + content);
		} catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
			System.out.println("Bot attempted to move a message (deletion) according to Admin module, however bot does not have the requisite permissions");
		}


	}

	public List<Class<Module>> getDependencies(){
		return null;
	}


}
