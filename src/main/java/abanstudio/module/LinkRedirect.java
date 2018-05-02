/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.CommandData;
import abanstudio.discordbot.BotServer;
import abanstudio.utils.sqlite.DBHandler;

import java.util.HashMap;
import java.util.List;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

/**
 * @author Anil James Banarsee
 */
public class LinkRedirect extends Module
{

	HashMap<String, IChannel> linkRedirChanMap;

	public LinkRedirect(BotServer server)
	{
		super(server);
		linkRedirChanMap = new HashMap<>();
	}

	@Override
	protected String getPrefix()
	{
		return null;
	}

	@Override
	public HashMap<String, Action> initializeActions()
	{
		return null;
	}

	@Override
	public List<CommandData> initializeCommData()
	{

		return null;
	}

	@Override
	public void onReady()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void initLinkRedirectChannel(IGuild guild)
	{
		String id = DBHandler.getGuildSetting(guild.getStringID(), "link_chan");
		IChannel chan = guild.getChannelByID(Long.parseLong(id));
		linkRedirChanMap.put(guild.getStringID(), chan);
	}

	@Override
	public String getName()
	{
		return "LinkRedirect";
	}


	public List<Class<Module>> getDependencies(){
		return null;
	}

}
