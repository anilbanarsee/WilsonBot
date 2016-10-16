/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.utils.sqlite.DBHandler;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args){

        /*DBHandler.banClip("420", "0241");
        DBHandler.banClip("420", "0242");
        DBHandler.banClip("420", "0243");
        DBHandler.banClip("420", "0244");*/
        
        System.out.println(DBHandler.getBanners("420"));

    }
    
}
