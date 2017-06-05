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
import abanstudio.games.GameTuple;
import abanstudio.games.PokemonGuessGame;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    
    HashMap<String, HashMap<String, GameTuple>> gameMap; 
    HashMap<String, Class> gameClassMap;
    

    
    Game game;
    
    List<ArrayList<String>> pokemon;
    

    public Games(BotServer server) throws InvalidGameClassException{
        super(server);
        initalizeGameMap();
        initalizeClassMap();
        //initalizeGameMap()
    }
    private void addGameClass(String n, Class c) throws InvalidGameClassException{
        if(!Game.class.isAssignableFrom(c)){
            throw new InvalidGameClassException(c.toString());
        }
        gameClassMap.put(n,c);
    }
    private void initalizeClassMap() throws InvalidGameClassException{
        gameClassMap = new HashMap<>();
        addGameClass("pokemon", PokemonGuessGame.class);
    }
    private void initalizeGameMap(){
        gameMap = new HashMap<>();
        List<IGuild> guilds = client.getGuilds();
        for(IGuild g : guilds){
            HashMap<String, GameTuple> hmap = new HashMap<>();
            gameMap.put(g.getID(), hmap);
        }
    }
    public GameTuple getGameTuple(String gid, String thread){
        return gameMap.get(gid).get(thread);
    }
    public void setGameTuple(String gid, String thread, GameTuple tuple){
        gameMap.get(gid).put(thread, tuple);
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
         
         String game = arguments[0];
         String[] gameArgs = new String[arguments.length-1];
         for(int i = 1; i<arguments.length; i++){
             gameArgs[i-1] = arguments[i];
         }
         
         GameTuple tuple = getGameTuple(message.getGuild().getID(),game);
         if(tuple == null){
             Class gameClass = gameClassMap.get(game);
             if(gameClass == null){
                 server.sendMessage(message.getChannel(),"No game named "+game+" was found.");
                 return;
             }
             
             Game gameObj = null;
             try {
                 gameObj = (Game) gameClass.getConstructor(server.getClass(),message.getChannel().getClass(),gameArgs.getClass()).newInstance();
             } catch (NoSuchMethodException ex) {
                 Logger.getLogger(Games.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SecurityException ex) {
                 Logger.getLogger(Games.class.getName()).log(Level.SEVERE, null, ex);
             } catch (InstantiationException ex) {
                 Logger.getLogger(Games.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IllegalAccessException ex) {
                 Logger.getLogger(Games.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IllegalArgumentException ex) {
                 Logger.getLogger(Games.class.getName()).log(Level.SEVERE, null, ex);
             } catch (InvocationTargetException ex) {
                 Logger.getLogger(Games.class.getName()).log(Level.SEVERE, null, ex);
             }
             if(gameObj == null){
                 return;
             }
             tuple = new GameTuple(gameObj);
             setGameTuple(message.getGuild().getID(),game,tuple);
         }
         
         
        
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
