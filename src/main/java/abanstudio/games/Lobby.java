package abanstudio.games;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;


public class Lobby
{
	ArrayList<IUser> players;
	int maxPlayers, minPlayers;
	Game game;
	long code;
	Thread thread;
	boolean gameStarted;
	IChannel channel;

	public Lobby(Game game)
	{
		players = new ArrayList<>();
		maxPlayers = game.getMaxPlayers();
		minPlayers = game.getMinPlayers();
		this.game = game;
		gameStarted = false;
		channel = game.channel;
	}
	public void setCode(Long code){
		this.code = code;
	}
	public Long getCode(){
		return code;
	}
	public boolean addPlayer(IUser user)
	{
		if (players.size() < maxPlayers || maxPlayers == -1) {
			if (!players.contains(user)) {
				players.add(user);
				return true;
			}
		}
		return false;

	}

	public boolean hasGameStarted()
	{
		return gameStarted;
	}

	public Game getGame()
	{
		return game;
	}

	public boolean canStart()
	{

		return (players.size() >= minPlayers || minPlayers == -1);
	}
	public boolean startGame(){
		if(hasGameStarted()){
			return false;
		}
		if(!canStart()){
			return false;
		}
		thread = new Thread(game);
		thread.start();
		gameStarted = true;
		return true;
	}
	public IChannel getChannel(){
		return channel;
	}
}
