/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.djdog;

import abanstudio.wilsonbot.Main;

/**
 *
 * @author User
 */
public class Looper implements Runnable{

    String guildID;
    
    public Looper(String GID){
        guildID = GID;
    }
    
    @Override
    public void run() {
        boolean flag = true;
        
        while(flag){
            Main.djdogClient.getGuildByID(guildID).getAudioChannel().
        }
    }
    
}
