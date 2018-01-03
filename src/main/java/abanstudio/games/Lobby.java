package abanstudio.games;

import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

public class Lobby {
    ArrayList<IUser> players;
    int maxPlayers, minPlayers;
    Game game;

    public Lobby(Game game){
        players = new ArrayList<>();
        maxPlayers = game.getMaxPlayers();
        minPlayers = game.getMinPlayers();
        this.game = game;
    }
    public boolean addPlayer(IUser user){
        if(players.size()<maxPlayers || maxPlayers==-1){
            if(!players.contains(user)) {
                players.add(user);
                return true;
            }
        }
        return false;

    }
    public boolean canStart(){

        return (players.size()>=minPlayers);
    }
}
