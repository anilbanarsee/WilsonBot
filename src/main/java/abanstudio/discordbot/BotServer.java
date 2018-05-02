/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import abanstudio.command.CommandData;
import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.command.CoreAction;
import abanstudio.exceptions.InvalidSearchException;
import abanstudio.module.Module;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEmbedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import javax.management.RuntimeErrorException;

/**
 * This is the core server class a.k.a. the root module. All modules build onto this class, and in turn this class
 * handles merging module commands and methods.
 * @author Anil James Banarsee
 */
abstract public class BotServer
{


	public IDiscordClient client;
	protected ArrayList<IRole> roles;
	public Matcher matcher;
	protected HashMap<String, Module> moduleMap;
	protected HashMap<String, Action> overrideMap;
	protected HashMap<String, CoreAction> overrides;
	protected HashMap<String, CoreAction> eventListeners;
	protected EventListener listener = null;
	//protected ArrayList<Module> modules;
	protected Module onMessageOverrider = null;
	protected Command base;


	public String prefix;

	public BotServer(IDiscordClient client)
	{
		this.client = client;
		//modules = new ArrayList<>();
		overrides = new HashMap<>();
		overrideMap = new HashMap<>();
		eventListeners = new HashMap<>();
		moduleMap = new HashMap<>();
		base = initializeCommands();
		initalizeEventListener();


	}

	@EventSubscriber
	public void onReady(ReadyEvent event)
	{

		onServerReady(event);
		System.out.println("Modules setting up...");
		for (Module m : moduleMap.values()) {
			System.out.println("[" + m + "]");
			m.onReady();
			System.out.println("Module :" + m + ", ready");
		}
		System.out.println("Modules ready");
		System.out.println("Setup complete");


	}

	protected abstract void onServerReady(ReadyEvent event);

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event)
	{

		CoreAction action = overrides.get("onMessage");
		if (action != null) {
			action.exec(event);
		} else {
			if (event.getMessage().getAuthor().isBot())
				return;
			String message = event.getMessage().getContent();

			if (message.startsWith(prefix + " ")) {


				String command = message;
				parseCommand(command, event.getMessage());

			}
		}

	}

	@EventSubscriber
	public void onDisconnect(DisconnectedEvent event) throws DiscordException
	{
		System.out.println("Bot disconnected with reason " + event.getReason() + ". Reconnecting...");
	}

	protected abstract HashMap<String, Action> initalizeActions();

	protected abstract List<CommandData> initalizeCommData();

	protected abstract void initalizeEventMethods();

	private void initalizeEventListener()
	{
		if (listener == null) {
			listener = new EventListener();
			client.getDispatcher().registerListener(listener);
		} else {
			client.getDispatcher().unregisterListener(listener);
		}

		listener.getMethodMap();
		Method[] methods = this.getClass().getMethods();
		ArrayList<Method> coreMethods = new ArrayList<>();

		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			if (method.getParameterCount() == 1) {
				for (Annotation annotation : annotations) {
					//System.out.println(c);
					if (annotation.annotationType().equals(EventSubscriber.class)) {
						coreMethods.add(method);
						break;
					}
				}
			}
		}
		for (Method m : coreMethods) {

			//MethodTuple mt = listener.getMethodMap().get(m.getParameterTypes()[0]);
			ArrayList<MethodTuple> mtlist = new ArrayList<>();
			mtlist.add(new MethodTuple(m, this));
			ArrayList<MethodTuple> mt = listener.getMethodMap().putIfAbsent(m.getParameterTypes()[0], mtlist);

			if (mt != null) {
				System.out.println("WARNING : On initialization a botserver : " + this.getClass() + " attempted to overwrite it's own event listener " + m + ". This usually occurs because you have two methods with @EventSubscriber and with the same parameter event type. The second method was ignored");
			}
		}
		System.out.println("");


	}
	private Command initializeCommands()
	{
		/*Initialize action and subcommand maps based on abstract methods*/
		HashMap<String, Action> actionMap = initalizeActions();
		List<CommandData> commDataList = initalizeCommData();

		/*Set origin for all activities*/
		for(Action a: actionMap.values()) {
			a.setOrigin(this);
		}
		//Create base command data
		CommandData data = new CommandData(prefix, prefix, "Base bot commands");
		//Create base command
		Command command = new Command(null, data, null);
		Command cP = command; //Pointer to current command
		for(CommandData commData : commDataList) {
			/*String containing cumulation of all strings looked at so far within commData.getComm(). For example:
			for the command string "comm:sub1:sub2", the variable will be updated as follows:
			 ""
			 "comm"
			 "comm:sub1"
			 "comm:sub1:sub2"
			 */
			String cSubC = "";

			/*Loop through all command names within the command string, seperated by :*/
			for(String subC : commData.getComm().split(":")) {

				/*Build up the command cumulation string*/
				if(cSubC.equals(""))
					cSubC = subC;
				else
					cSubC = cSubC + ":" + subC;

				/*Get matching action from actionMap*/
				Action a = actionMap.get(cSubC);
				if(a == null) {
					System.out.println("Error: Attempting to create command "+commData.getComm()+" but "+cSubC+" does " +
							"not exist on actionMap of server class");
					System.exit(0);
				}

				/*Get matching subcommand from pointer. If null then create a new command with current action and
				commData. Otherwise, set the pointer to that command.
				 */
				Command c = cP.getSubCommand(subC);
				if(c==null) {
					CommandData cData = new CommandData(commData.getRegex(), subC, commData.getDescS());
					cP.addSubCommand(new Command(a, cData, cP));
				}
				else {
					cP = c;
				}
			}
		}
		return command;
	}
	/* Creates command and subcommand objects based upon Command Data map and Action map*/
	/*
	private void initializeCommands()
	{
		//Initialize action and commdata maps, these methods will have been extended by any subclass of this
		HashMap<String, Action> actionMap = initalizeActions();
		String[][] commData = initalizeCommData();

		//Set origin of all actions to be this class
		for (Action a : actionMap.values()) {
			a.setOrigin(this);
		}
		for (Action a : overrideMap.values()) {
			a.setOrigin(this);
		}

		commands = new ArrayList<>();
		//Start with actionMap. Loop through all key value pairs and for each check for an existing comm data entry.
		for (Entry<String, Action> entry : actionMap.entrySet()) {

			String[] cData = {};
			boolean flag = false;

			for (String[] array : commData) {
				String[] split = array[1].split(":");
				Command c = null;
				for(int i=0; i<split.length; i++){
					if(split[i].equals("")) continue;
						if (array[Command.NCOMM].equals(entry.getKey())){
							if(c==null) {

							}
							else {

							}
						}
					}
				}
				if (array[Command.NCOMM].equals(entry.getKey())) {
					flag = true;
					cData = array;
				}
			}

			String key = "";
			if (!flag) {
				key = entry.getKey();
				cData = new String[3];
				cData[0] = key;
				cData[1] = key;
				cData[2] = "No information sry :(";
				key = entry.getKey();
			} else {
				key = cData[1];
			}


			int n;
			try {
				n = Integer.parseInt(cData[cData.length - 1]);
				commands.add(new Command(actionMap.get(key), cData, n));
			} catch (NumberFormatException e) {
				commands.add(new Command(actionMap.get(key), cData));
			}




		}
		if (!overrideMap.isEmpty())
			for (Entry<String, Action> entry : overrideMap.entrySet()) {

				String[] cData = {};
				boolean flag = false;

				for (String[] array : commData) {
					if (array[1].equals(entry.getKey())) {
						flag = true;
						cData = array;
					}
				}

				String key = "";
				if (!flag) {
					key = entry.getKey();
					cData = new String[3];
					cData[0] = key;
					cData[1] = key;
					cData[2] = "No information sry :(";
					key = entry.getKey();
				} else {
					key = cData[1];
				}


				int n = -345;
				boolean parsed = false;
				try {
					n = Integer.parseInt(cData[cData.length - 1]);
					parsed = true;
				} catch (NumberFormatException e) {
					commands.add(new Command(overrideMap.get(key), cData));

				}
				if (parsed)
					commands.add(new Command(overrideMap.get(key), cData, n));


			}


	}*/

	@EventSubscriber
	public void onFileRecieved(MessageEmbedEvent event)
	{
		System.out.println("File Recieved Event");
	}
	public boolean parseCommand(String input, IMessage message){
		input = input.substring(prefix.length()+1);
		String[] split = input.split(" ");

		boolean finished = false;
		Command cPoint  = base;
		int i = 0; int cNum = split.length;
		while(i<cNum && !finished){
			String commString = split[i];
			Command c = cPoint.getSubCommand(commString);
			if(c!=null) {
				cPoint = c;
			}else{
				finished = true;
			}
			i++;
		}
		if (canUse(cPoint.getInfo().getAdminLevel(), message.getAuthor(), message.getGuild())){
			String[] args = new String[split.length - (i-1)];
			for(int j = i-1; j<split.length; j++){
				args[j] = split[j];
			}
			cPoint.getAction().exec(args, message);
			return true;
		}
		else{
			sendMessage(message.getChannel(), "You do not have permission to use this command");
			return false;
		}
	}
	/*
	public boolean parseCommand(String input, IMessage message)
	{

		input = input.substring(prefix.length()+1);
		String[] split = input.split(" ");
		Command loaded = null;
		ArrayList<String> args = new ArrayList<>();
		boolean isCommand = false;

		for (String s : split) {
			if (s.equals("")) {
				continue;
			}
			Command c = Command.matchCommand(commands, s);

			if (c != null) {
				if (loaded != null) {
					if (canUse(loaded.getAdminLevel(), message.getAuthor(), message.getGuild())) {
						String[] array = new String[args.size()];
						loaded.getAction().exec(args.toArray(array), message);
						args.clear();
						isCommand = true;
					} else {
						sendMessage(message.getChannel(), "You do not have permission to use this command");
					}
				}
				loaded = c;
			} else {
				args.add(s);
			}
		}
		if (loaded != null) {
			if (canUse(loaded.getAdminLevel(), message.getAuthor(), message.getGuild())) {
				String[] array = new String[args.size()];
				loaded.getAction().exec(args.toArray(array), message);
				args.clear();
				isCommand = true;
			} else {
				sendMessage(message.getChannel(), "You do not have permission to use this command");
			}
		}


		return isCommand;
	}*/

	public void addModule(Module module)
	{

		System.out.println("Adding module: "+module.getName());
		Method[] methods = module.getClass().getMethods();
		ArrayList<Method> coreMethods = new ArrayList<>();
		ArrayList<Method> replaceMethods = new ArrayList<>();

		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			if (method.getParameterCount() == 1) {
				boolean eSub = false;
				boolean replace = false;
				for (Annotation annotation : annotations) {

					if (annotation.annotationType().equals(EventSubscriber.class)) {
						eSub = true;
					}
					if (annotation.annotationType().equals(Replaces.class)) {
						replace = true;
					}
				}
				if (eSub) {
					if (replace) {
						replaceMethods.add(method);
					} else {
						coreMethods.add(method);
					}
				}
			}
		}
		for (Method m : coreMethods) {

			ArrayList<MethodTuple> mt = listener.getMethodMap().get(m.getParameterTypes()[0]);

			if (mt == null) {
				mt = new ArrayList<>();
				mt.add(new MethodTuple(m, module));
				listener.getMethodMap().put(m.getParameterTypes()[0], mt);
				System.out.println("Module " + module + " added listener event " + m.getName() + " successfully. There were no previous methods that were overridden");

			} else {
				mt.add(new MethodTuple(m, module));
				System.out.println("Module " + module + " added listener event " + m.getName() + " successfully.");

			}

		}
		for (Method m : replaceMethods) {
			ArrayList<MethodTuple> mt = listener.getMethodMap().get(m.getParameterTypes()[0]);

			if (mt == null) {
				mt = new ArrayList<>();
				mt.add(new MethodTuple(m, module));
				System.out.println("Module " + module + " successfully added method " + m + " to event listener. There was no previous implementation of this signature");
			} else {
				boolean added = false;
				for (MethodTuple tuple : mt) {
					if (BotServer.class.isAssignableFrom(tuple.getObject().getClass())) {
						tuple.setMethodAndObject(m, module);
						added = true;
						System.out.println("Module " + module + " successfully overrided BotServer implementation of method " + m + ".");
						break;
					}
				}
				if (!added) {
					mt.add(new MethodTuple(m, module));
					System.out.println("Module " + module + " successfully added EventListener method " + m + ". There was no previous BotServer implementation of this method");
				}
			}
		}

//		Command c = module.getRootCommand();
//		HashMap<String, Action> overrideActions = module.getOverrides();
//		HashMap<String, Action> actionMap = module.initalizeAction

		HashMap<String, Action> actionMap = module.initializeActions();
		List<CommandData> commDataList = module.initializeCommData();


		int commRejected = 0;
		int commAdded = 0;
		int commOver = 0;

		Command modBase = new Command(new Action()
		{
			@Override
			public void exec(String[] arguments, IMessage message)
			{
				callModuleMethod(module.getName(), arguments);
			}
		}, new CommandData(module.getName(), module.getName(), "Command for "+module.getName()), this.base);

		Command cPS = this.base; //Pointer to command root of this server
		Command cPM = modBase;

		if(cPS.addSubCommand(cPM)!=null){
			System.out.println("Error adding module "+module.getName()+" to botserver, subcommand '"+module.getName()+"' already " +
					"exists as a root subcommand");
			System.exit(0);
		}

		for (CommandData commData: commDataList){

			Action a = actionMap.get(commData.getComm());
			if(a == null) {
				throw new java.lang.Error("Error adding module " + module.getName() + ", command " + commData.getComm() + " did not " +
						"have a matching action in it's actionMap");
			}

			String[] cSplit = commData.getComm().split(":");
			String sCurrent = "";
			for(int i=0; i<cSplit.length; i++){
				if(a.getOverride()){

					sCurrent += cSplit[i];
					if(i!=cSplit.length-1){
						sCurrent += ":";
					}
					Command c = cPS.getSubCommand(cSplit[i]);
					if(c==null){
						throw new java.lang.Error("Error adding module "+module.getName()+", command "+commData.getComm()+" is " +
								"an override command and the base botserver did not have a command matching '"+sCurrent+"'.");
					}
					if(i==cSplit.length-1){
						if(c.getInfo().getComm().equals(commData.getComm())) {
							if(Module.class.isAssignableFrom(c.getAction().getOrigin().getClass())){
								System.out.println("Module " + module + " attempted to override base command '" + c.getInfo().getComm() + "'. However, this was already overrided by module " + c.getAction().getOrigin());
								System.exit(0);
							}
							c.setAction(a);
							c.setCommandData(commData);
							System.out.println("Module " + module + " successfully overrided base command '" + c.getInfo().getComm() + "'.");
							commOver++;
						}
						else{
							throw new java.lang.Error("Error adding module "+module.getName()+", command "+commData.getComm()+" is " +
									("an override command but command '"+commData.getComm()+"' does not match target override command '"+c.getInfo().getComm()+"'"));
						}
					}
					else{
						cPS = c;
					}
				}else{
					sCurrent += cSplit[i];
					if (i != cSplit.length-1) sCurrent += ":";
					Command c = cPM.getSubCommand(cSplit[i]);

					if(c==null){
						Action act = actionMap.get(sCurrent);
						if( act == null){
							throw new java.lang.Error("Error adding module "+module.getName()+", command "+sCurrent+" did not " +
									"have a matching action in its actionMap");
						}
						c = new Command(act, new CommandData(cSplit[i], cSplit[i], commData.getDesc()), null);
						cPM.addSubCommand(c);
						cPM = c;
					}
					else {
						cPM = c;
					}
				}
			}
			commAdded++;
		}
		moduleMap.put(module.getName(), module);
		/*for (Command cX : moduleCommands) {
			boolean flag = true;
			for (Command cY : this.commands) {
				if (cX.getComm().equals(cY.getComm())) {
					Action a = overrideActions.get(cX.getComm());
					if (a != null) {
						if (Module.class.isAssignableFrom(cY.getAction().getOrigin().getClass())) {
							System.out.println("Module " + module + " attempted to override base command '" + cY.getComm() + "'. However, this was already overrided by module " + cY.getAction().getOrigin());
						} else {
							cY.setAction(a);
							commOver++;
							System.out.println("Module " + module + " sucessfully overrided base command '" + cY.getComm() + "'.");
							break;
						}
					} else {

						System.out.println("Error : Module " + module.getName() + " tried to add command " + cX.getComm() + " which was already present. Skipping this command, module will still be added. Add the command to 'overrides' in the module class if you want to override an existing command in a botserver");
						flag = false;
						commRejected++;
						break;
					}
				}
			}
			if (flag) {
				this.commands.add(cX);
				commAdded++;
			}
		}*/
		System.out.println("Loaded module '" + module.getName() + "'. Commands added :" + commAdded + ". Commands rejected due to merge conflicts :" + commRejected + ". Base commands ovewritten " + commOver + ".");
	}

	public boolean callModuleMethod(String moduleS, String[] command){
		return true;
	}

	public boolean matches(String s, String regex)
	{
		Pattern p = Pattern.compile(regex);
		matcher = p.matcher(s);

		return matcher.find();
	}

	public void sendMessage(IChannel channel, String message)
	{

		RequestBuffer.request(() ->
		{
			try {
				new MessageBuilder(client).withChannel(channel).withContent(message).build();
			} catch (DiscordException | MissingPermissionsException e) {
				e.printStackTrace();
			}
			return null;
		});

	}

	public void sendMessage(IUser user, String message)
	{

		RequestBuffer.request(() ->
		{
			try {
				new MessageBuilder(client).withChannel(user.getOrCreatePMChannel()).withContent(message).build();
			} catch (DiscordException | MissingPermissionsException e) {
				e.printStackTrace();
			}
			return null;
		});

	}


	public void join(String[] arguments, IMessage message)
	{
		IChannel channel = message.getChannel();

		if (arguments.length == 0) {
			sendMessage(channel, "Tell me where you want me to go, give a channel name or use 'me' if you want me to join you");
		}
		String argument = arguments[0];
		for (int i = 1; i < arguments.length; i++) {
			argument += " " + arguments[i];
		}

		if (argument.equals("me")) {


			try {
				message.getAuthor().getVoiceStateForGuild(message.getGuild()).getChannel().join();

			} catch (MissingPermissionsException ex) {
				sendMessage(message.getChannel(), "I don't have permissions to join that channel");
			}


		} else {
			List<IVoiceChannel> chanList = message.getGuild().getVoiceChannelsByName(argument);
			if (chanList.size() > 1) {
				sendMessage(message.getChannel(), "This server contains two channels with the name " + argument + ". I may not have joined the correct one.");
			}
			try {
				chanList.get(0).join();
			} catch (MissingPermissionsException ex) {
				sendMessage(message.getChannel(), "I don't have permissions to join that channel");
			}

		}


	}

	public void leave(IMessage message)
	{
		String guildID = message.getGuild().getStringID();

		IVoiceChannel vc;
		try {
			vc = getVoiceChannel(guildID);
		} catch (InvalidSearchException ex) {
			return;
		}

		vc.leave();

	}

	public IVoiceChannel getVoiceChannel(String guildID) throws InvalidSearchException
	{

		List<IVoiceChannel> channels = client.getConnectedVoiceChannels();

		for (IVoiceChannel chan : channels) {
			if (chan.getGuild().getStringID().equals(guildID)) {
				return chan;
			}
		}

		throw new InvalidSearchException();

	}

	public static void sendFile(IChannel channel, File f)
	{

		try {
			channel.sendFile(f);
		} catch (IOException | MissingPermissionsException | RateLimitException | DiscordException ex) {
			Logger.getLogger(BotServer.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public boolean canUse(int adminLevel, IUser user, IGuild guild)
	{

		if (adminLevel == 0) {
			return true;
		} else if (adminLevel >= 1) {
			return isAdmin(user, guild);
		} else {
			return false;
		}

	}

	public boolean isAdmin(IUser user, IGuild guild)
	{


		List<IRole> roles = guild.getRolesForUser(user);
		//System.out.println("Admin :"+Permissions.ADMINISTRATOR.ordinal());
		for (IRole role : roles) {

			System.out.println(role.getName());
			for (Permissions p : role.getPermissions()) {
				if (p.hasPermission(Permissions.ADMINISTRATOR.ordinal())) {
					return true;
				}
			}
		}
		return false;
	}
}
