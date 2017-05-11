/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games;

import abanstudio.discordbot.BotServer;
import java.util.Random;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author General
 */
public class RussianRoulette extends Game{
    
    int chambers;
    
    public RussianRoulette(BotServer server){
        super(server);
        chambers = 6;
    }
    public RussianRoulette(BotServer server, int chambers){
        this(server);
        this.chambers = chambers;
    }
    
    @Override
    public void startgame(IMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {

        
        
    }
    public boolean shoot(){
        System.out.println("Shooting, with "+chambers+" chambers remaining");
        Random r = new Random();
        int shot = r.nextInt(chambers);
        chambers--;
        return shot==0;
    }

    @Override
    public int getJoinRule() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStartAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
