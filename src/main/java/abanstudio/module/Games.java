package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.command.CommandData;
import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;
import abanstudio.games.Lobby;
import abanstudio.utils.Text;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Games extends Module
{


	private HashMap<String, Class> gameClassMap;
	private HashMap<Long, HashMap<Long, Lobby>> lobbyMap;
	private ArrayList<Long> activeLobbies;
	private ArrayList<String> gameData;


	public Games(BotServer server)
	{
		super(server);
		gameClassMap = new HashMap<>();
	}

	@Override
	public HashMap<String, Action> initializeActions()
	{

		HashMap<String, Action> actionMap = new HashMap<>();
		actionMap.put("createlobby", new Action()
		{
			@Override
			public void exec(String[] args, IMessage m)
			{
				createLobby(args, m);
			}
		});
		actionMap.put("joinlobby", new Action(){@Override public void exec(String[] args, IMessage m){joinLobby(args, m);}});

		return actionMap;
	}

	@Override
	public List<CommandData> initializeCommData()
	{
		ArrayList<CommandData> cData = new ArrayList<>();

		cData.add(new CommandData("[cC]reatelobby", "createlobby", "Creates a lobby for a given game"));
		cData.add(new CommandData("[jJ]oinlobby", "joinlobby", "Joins and existing lobby from lobby code"));
		//cData.add(new CommandData("[lL]ist", "list", "listtest"));
		//cData.add(new CommandData("[lL]obbies", "list:lobbies", "listsubtest"));

		return cData;
	}

	protected void initalizeGameData()
	{
		gameData = new ArrayList<String>();

	}
	protected void initializeLobbyMap()
	{
		lobbyMap = new HashMap<>();
		for (IGuild guild : client.getGuilds()) {
			lobbyMap.put(guild.getLongID(), new HashMap<>());
		}
	}

	public boolean addGameClass(String n, Class c)
	{

		if (!Game.class.isAssignableFrom(c)) {
			System.out.println("Attempted to add " + c.getName() + " to game class map. However this is not a game class");
			return false;
		}
		gameClassMap.put(n, c);
		return true;
	}

	@Override
	public void onReady()
	{


		initializeLobbyMap();

	}

	@Override
	public String getName()
	{
		return "Games";
	}

	@Override
	public String getPrefix(){ return "games"; }
	/*Lists information relating to this module*/
	public void list(String[] args, IMessage m){
		//Not enough arguments
		if(args.length<1){
			sendMessage(m.getChannel(), "You need to specify what to list, you can list lists with 'list lists'");
			return;
		}
		//List active lobbies
		if(args[0].equals("lobbies")){
			//Generate table headers
			String[] head = {"ID","Name","Started?","Players","Min Players to Start","Can Start?"};
			//Generate lobby data and insert into matrix
			String[][] lobbyData = new String[activeLobbies.size()][5];
			HashMap<Long, Lobby> lobbies = lobbyMap.get(m.getGuild().getLongID());
			for(int i = 0; i<activeLobbies.size(); i++){
				Lobby l = lobbies.get(i);
				lobbyData[i][0] = i+"";
				lobbyData[i][1] = l.getGame().getName();
				lobbyData[i][2] = String.valueOf(l.hasGameStarted());
				lobbyData[i][3] = l.getGame().getPlayers().size()+"/"+l.getGame().getMaxPlayers();
				lobbyData[i][4] = l.getGame().getMinPlayers()+"";
				lobbyData[i][5] = String.valueOf(l.canStart());
			}
			//Convert table data into string
			String table_string = Text.makeTable(head,lobbyData);
			table_string = "```"+table_string+"```";
			sendMessage(m.getChannel(),table_string);
		}
		//List available games for this guild
		else if(args[0].equals("games")){
			//todo
		}
	}
	public void joinLobby(String[] args, IMessage m){
		if(args.length < 1){
			sendMessage(m.getChannel(), "You need to give a lobby code when joining a lobby. You can view " +
					"existing lobbies using 'list lobbies'.");
			return;
		}
		long l;
		try {
			l = Long.parseLong(args[0]);
		} catch(NumberFormatException e){
			sendMessage(m.getChannel(), args[0]+" was not a valid lobby code.");
			return;
		}

		HashMap<Long, Lobby> map = lobbyMap.get(m.getGuild().getLongID());

		if(map == null){
			sendMessage(m.getChannel(), "Error, lobbies cannot be created on this guild. Try again later or contact Master Admin");
			return;
		}

		Lobby lobby = map.get(l);

		if(lobby == null){
			sendMessage(m.getChannel(), "There is no lobby with the code "+l+" on this guild. You can view current lobbies with 'list lobbies'");
			return;
		}

		boolean b = lobby.addPlayer(m.getAuthor());

		if(!b){
			sendMessage(m.getChannel(), "Could not join lobby "+l+". Lobby full.");
			return;
		}

		sendMessage(m.getChannel(), "Successfully joined lobby");
		IChannel channel = lobby.getChannel();

		IRole role = m.getGuild().getEveryoneRole();
		for(Permissions p : role.getPermissions()){
			System.out.println(p);
		}





	}
	public void createLobby(String[] args, IMessage m)
	{
		if (args.length < 1) {
			sendMessage(m.getChannel(), "You need to specify a game");
			return;
		}
		long code = -1;
		HashMap<Long, Lobby> tempMap = lobbyMap.get(m.getGuild().getLongID());
		int MAX_LOBBIES = 5;
		for(int i = 0; i< MAX_LOBBIES; i++){
			 if(tempMap.get(i)!=null){
			 	code = i;
			 	break;
			 }

		}
		if(code==-1){
			return;
		}
		String gameString = args[0];
		Class gameClass = gameClassMap.get(gameString);
		if (gameClass == null) {
			sendMessage(m.getChannel(), "There's no game with the name " + gameString + ".");
			return;
		}
		IChannel tempChannel = m.getGuild().createChannel(gameString + "_1");
		Game game = null;
		try {
			game = (Game) gameClass.getConstructor(BotServer.class, IChannel.class).newInstance(server, tempChannel);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (game == null) {
			sendMessage(m.getChannel(), "There was an error attempting to start game " + gameString + ".");
			return;
		}

		Lobby lobby = new Lobby(game);



        lobbyMap.get(m.getGuild().getLongID()).put(code, lobby);
		activeLobbies.add(code);
        sendMessage(m.getChannel(), "Lobby for "+game.toString()+" started in channel: "+tempChannel.getName());






	}

	public List<Class<Module>> getDependencies(){
		return null;
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event)
	{
		HashMap<Long, Lobby> map = lobbyMap.get(event.getGuild().getLongID());
		if (map != null) {
			Lobby lobby = map.get(event.getChannel().getLongID());
			if (lobby != null) {
				if (lobby.hasGameStarted()) {
					lobby.getGame().message(event.getMessage());
				}
			}
		}
	}

}
