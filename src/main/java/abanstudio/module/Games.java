/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.discordbot.BotServer;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.games.Game;
import abanstudio.games.PokemonGuessGame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IGuild;

/**
 *
 * @author General
 */
public class Games extends Module{

    Thread gameThread;
    
    HashMap<String, HashMap<String, Game>> map;
    HashMap<String, HashMap<String, Thread>> threadmap;
    String[] gameList = {"G1","G2","G3","G4","G5"};
    

    
    Game game;
    
    List<ArrayList<String>> pokemon;
    

    public Games(BotServer server){
        super(server);
        map = new HashMap<>();
    }
    private void initalizeGameMap(){
        map = new HashMap<>();
        List<IGuild> guilds = client.getGuilds();
        for(IGuild g : guilds){
            HashMap<String, Game> hmap = new HashMap<>();
            map.put(g.getID(), hmap);
        }
    }
    private Game getGame(String gid, String thread){
        return map.get(gid).get(thread);
    }
    private Thread getThread(String gid, String thread){
        return threadmap.get(gid).get(thread);
    }
    private void setGame(String gid, String thread, Game g){
        map.get(gid).put(thread, g);
    }
    private void setThread(String gid, String thread, Thread t){
        threadmap.get(gid).put(thread, t);
    }
    @Override
    protected void initalizeActions() {
        actionMap = new HashMap<>();
        
        actionMap.put("ping",  new Action(){public void exec(String[] arg, IMessage m) {ping(arg,m);}});  
        actionMap.put("game",new Action(){public void exec(String[] arg, IMessage m) {game(arg,m);}});
        actionMap.put("guess",new Action(){public void exec(String[] arg, IMessage m) {guess(arg,m);}});
  
    }
    
    public void onMessage(MessageReceivedEvent event){
        
         server.sendMessage(event.getMessage().getChannel(), "pong");
        
    }
    @EventSubscriber
    public void testEvent(Event event){
        System.out.println("Event received");
        System.out.println(event.getClass().getName());
    }
    
    
    @Override
    protected void initalizeCommData() {
            String[][] comms = {{"[pP]ing","ping","test","0"},
                {"[gG]ame","game","starts a game","dog game start [gameType]","0"},
                {"[gG]uess","game","guesses an answer for the game","dog guess [answer]","0"}
                                    };
        commData = comms;
    }
    
     public void game(String[] arguments, IMessage message){

       
         
        
    }
    
    public void joinGame(String[] args, IMessage message){
        
    }
    

    public void guess(String[] arguments, IMessage channel){
        
        PokemonGuessGame g = (PokemonGuessGame) game;
        g.guessPokemon(arguments[0]);
           
        
    }

    @Override
    public String getName() {
        return "Games";
    }
    
    public void onReady(){
        
    }
    public void ping(String[] args, IMessage m){
        
    }


}
