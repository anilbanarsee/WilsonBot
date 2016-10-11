/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.utils.sqlite.DBHandler;
import abanstudio.wilsonbot.FFMPEG;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException{

        String[] tags = {"dota"};
        ArrayList<String[]> clips = DBHandler.getClips(tags);
        for(String[]  s : clips){
            System.out.println(Arrays.toString(s));
        }
    }
    
}
