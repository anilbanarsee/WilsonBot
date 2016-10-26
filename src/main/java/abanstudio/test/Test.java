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

        double d = 4.534434;
        System.out.println(Math.round((d%1)*10));
        double[] times = {d};
        System.out.println(d-(d%1));
        System.out.println((times[0]%1));
        double du = ((times[0]-(times[0]%1))*1000)+(times[0]%1)*1000;
        System.out.println(Math.round(du));
    }
    
}
