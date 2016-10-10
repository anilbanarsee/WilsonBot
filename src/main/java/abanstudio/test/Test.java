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

        
        AudioPlayer.Track track = new AudioPlayer.Track(new FileProvider("helloworld.mp3")); // Don't worry, I will be covering what "FileProvider" is in a moment.
        Map<String, Object> metadata = track.getMetadata();
        System.out.println("hello");

    }
    
}
