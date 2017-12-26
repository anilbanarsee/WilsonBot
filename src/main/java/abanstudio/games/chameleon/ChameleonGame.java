/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.chameleon;

import abanstudio.discordbot.BotServer;
import abanstudio.games.Game;
import java.util.Arrays;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Reetoo
 */
public class ChameleonGame extends Game {

    String[][] words;
    
    public ChameleonGame(BotServer server, IChannel channel) {
        super(server, channel);
        words = new String[4][4];
       
    }

    @Override
    public void startgame(IMessage message) {
         //server.sendMessage(message.getChannel(), "game starting");
         generateWords();
    }

    @Override
    public void endGame() {
         
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getJoinRule() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStartAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void generateWords(){
        String[] list = {"Indiana Jones", "Popeye", "Spiderman", "Darth Vader", "Sherlock Holmes", "Gandalf the Grey", "Superman", "Batman", "James Bond", "Dracula", "Homer Simpson", "Frankenstein", "Robin Hood", "Mario", "Tarzan", "Hercules"};
        int x = 0;
        for(String[] row: words){
            for(int i=0; i<row.length; i++){
                row[i] = list[x];
                x++;
            }
            System.out.println(Arrays.toString(row));
        }
        
    }
    
    
}
