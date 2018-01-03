package abanstudio.module;

import abanstudio.command.Action;
import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;
import abanstudio.games.GameTuple;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Games extends Module{


    HashMap<String, Class> gameClassMap;
    HashMap<Long, HashMap<Long, GameTuple>> gameObjMap;


    public Games(BotServer server) {
        super(server);
        gameClassMap = new HashMap<>();
    }

    @Override
    protected void initalizeActions() {

        actionMap = new HashMap<>();
        actionMap.put("startgame", new Action(){@Override public void exec(String[] args, IMessage m) {startGame(args,m);}});



    }

    @Override
    protected void initalizeCommData() {
        String[][] comms = {{"[sS]tartgame","startgame","Starts a game","0"}};
        commData = comms;

    }

    protected void initalizeGameObjMap(){
        gameObjMap = new HashMap<>();
        for(IGuild guild: client.getGuilds()){
            gameObjMap.put(guild.getLongID(), new HashMap<>());
        }
    }

    public boolean addGameClass(String n, Class c){

        if(!Game.class.isAssignableFrom(c)){
            System.out.println("Attempted to add "+c.getName()+" to game class map. However this is not a game class");
            return false;
        }
        gameClassMap.put(n, c);
        return true;
    }

    @Override
    public void onReady() {


        initalizeGameObjMap();

    }

    @Override
    public String getName() {
        return "Games";
    }

    public void startGame(String[] args, IMessage m){
        if(args.length<1){
            sendMessage(m.getChannel(), "You need to specify a game");
            return;
        }
        String gameString = args[0];
        Class gameClass = gameClassMap.get(gameString);
        if(gameClass == null){
            sendMessage(m.getChannel(), "There's no game with the name "+gameString+".");
            return;
        }
        IChannel tempChannel = m.getGuild().createChannel(gameString+"_1");
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
        if(game == null){
            sendMessage(m.getChannel(), "There was an error attempting to start game "+gameString+".");
            return;
        }


        /*
        Thread t = new Thread(game);
        GameTuple gt = new GameTuple(game, t);



        gameObjMap.get(m.getGuild().getLongID()).put(tempChannel.getLongID(), gt);

        sendMessage(m.getChannel(), "Game started in channel: "+tempChannel.getName());



        sendMessage(tempChannel, "Starting game...");
        game.startGame();
        t.start();*/
    }
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        HashMap<Long, GameTuple> map = gameObjMap.get(event.getGuild().getLongID());
        if(map!=null){
            GameTuple gt = map.get(event.getChannel().getLongID());
            if(gt!=null){
                gt.getGame().message(event.getMessage());
            }
        }
    }

}
