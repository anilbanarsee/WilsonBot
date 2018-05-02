package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.CommandData;
import abanstudio.discordbot.BotServer;

import java.util.HashMap;
import java.util.List;

public class TempCreate extends Module
{
	public TempCreate(BotServer server)
	{
		super(server);
	}

	@Override
	protected String getPrefix()
	{
		return "tcreate";
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

	}

	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public List<Class<Module>> getDependencies()
	{
		return null;
	}


}

