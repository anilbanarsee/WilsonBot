/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.command.CommandData;
import abanstudio.discordbot.BotServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

/**
 * @author Anil James Banarsee
 */
public abstract class Module
{

	protected BotServer server;
	protected IDiscordClient client;
//	protected String[][] commData;
//	protected ArrayList<Command> commands;
//	protected ArrayList<Command> overrides;
//	protected ArrayList<Command> overrideCommands;
//	protected HashMap<String, Action> actionMap;
//	protected HashMap<String, Action> overrideMap;
//	List<Module> modules;
//	protected Command base;
//	protected Command override;

	public Module(BotServer server)
	{
//		overrideMap = new HashMap<>();

		setServer(server);
		//initializeCommands();

	}
	protected abstract String getPrefix();

	public abstract HashMap<String, Action> initializeActions();

	public abstract List<CommandData> initializeCommData();

	public abstract void onReady();

	public abstract String getName();

	public abstract List<Class<Module>> getDependencies();

//	public void loadDependencies(List<Module> list){
//		modules = list;
//	}
//	protected final Command initializeCommands(){
//		/*Initialize action and subcommand maps based on abstract methods*/
//		HashMap<String, Action> actionMap = initializeActions();
//		List<CommandData> commDataList = initializeCommData();
//
//		/*Set origin for all activities*/
//		for(Action a: actionMap.values()) {
//			a.setOrigin(this);
//		}
//		//Create base command data
//		CommandData data = new CommandData(getPrefix(), getPrefix(), "Base module commands");
//		//Create base command
//		Command command = new Command(null, data, null);
//
//		for(CommandData commData : commDataList) {
//			/*String containing cumulation of all strings looked at so far within commData.getComm(). For example:
//			for the command string "comm:sub1:sub2", the variable will be updated as follows:
//			 ""
//			 "comm"
//			 "comm:sub1"
//			 "comm:sub1:sub2"
//			 */
//			String cSubC = "";
//			Command cP = command; //Pointer to current command
//			/*Loop through all command names within the command string, seperated by :*/
//			for(String subC : commData.getComm().split(":")) {
//
//
//				/*Build up the command cumulation string*/
//				if(cSubC.equals(""))
//					cSubC = subC;
//				else
//					cSubC = cSubC + ":" + subC;
//				/*Get matching action from actionMap*/
//				Action a = actionMap.get(cSubC);
//				if(a == null) {
//					System.out.println("Error: Attempting to create command "+commData.getComm()+" but "+cSubC+" does " +
//							"not exist on actionMap of server class");
//					System.exit(0);
//				}
//
//				/*Get matching subcommand from pointer. If null then create a new command with current action and
//				commData. Otherwise, set the pointer to that command.
//				 */
//				Command c = cP.getSubCommand(subC);
//				if(c==null) {
//					CommandData cData = new CommandData(commData.getRegex(), subC, commData.getDescS());
//					cP.addSubCommand(new Command(a, cData, cP));
//				}
//				else {
//					cP = c;
//				}
//			}
//		}
//		return command;
//	}
//	protected final void initalizeCommands()
//	{
//
//		HashMap<String, Action> actionMap = initializeActions();
//		List<CommandData> commData = initializeCommData();
//
//		for (Action a : actionMap.values()) {
//			a.setOrigin(this);
//		}
//
//		ArrayList<Command> commands = new ArrayList<>();
//
//		for (Entry<String, Action> entry : actionMap.entrySet()) {
//
//			String[] cData = {};
//			boolean flag = false;
//
//			for (CommandData cData : commData) {
//				if (cData.getComm().equals(entry.getKey())) {
//					flag = true;
//
//				}
//			}
//
//			String key = "";
//			if (!flag) {
//				key = entry.getKey();
//				cData = new String[3];
//				cData[0] = key;
//				cData[1] = key;
//				cData[2] = "No information sry :(";
//				key = entry.getKey();
//			} else {
//				key = cData[1];
//			}
//
//
//			int n = -345;
//			boolean parsed = false;
//			try {
//				n = Integer.parseInt(cData[cData.length - 1]);
//				parsed = true;
//			} catch (NumberFormatException e) {
//				commands.add(new Command(actionMap.get(key), cData));
//
//			}
//			if (parsed)
//				commands.add(new Command(actionMap.get(key), cData, n));
//
//
//		}
//		if (!overrideMap.isEmpty())
//			for (Entry<String, Action> entry : overrideMap.entrySet()) {
//
//				String[] cData = {};
//				boolean flag = false;
//
//				for (String[] array : commData) {
//					if (array[1].equals(entry.getKey())) {
//						flag = true;
//						cData = array;
//					}
//				}
//
//				String key = "";
//				if (!flag) {
//					key = entry.getKey();
//					cData = new String[3];
//					cData[0] = key;
//					cData[1] = key;
//					cData[2] = "No information sry :(";
//					key = entry.getKey();
//				} else {
//					key = cData[1];
//				}
//
//
//				int n = -345;
//				boolean parsed = false;
//				try {
//					n = Integer.parseInt(cData[cData.length - 1]);
//					parsed = true;
//				} catch (NumberFormatException e) {
//					commands.add(new Command(overrideMap.get(key), cData));
//
//				}
//				if (parsed)
//					commands.add(new Command(overrideMap.get(key), cData, n));
//
//
//			}
//
//
//	}
	private void setServer(BotServer server)
	{
		this.server = server;
		client = server.client;
	}
//	public Command getRootCommand(){
//		return base;
//	}
//	public ArrayList<Command> getCommands()
//	{
//		return commands;
//	}
//
//	public boolean overridesMethods()
//	{
//		return overrideMap.size() > 0;
//	}

	@Override
	public String toString()
	{
		return getName();
	}

	public void sendMessage(IChannel channel, String message)
	{
		server.sendMessage(channel, message);
	}

}

