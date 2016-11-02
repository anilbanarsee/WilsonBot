/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.utils.sqlite.DBHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args){
        ArrayList<String> test = new ArrayList<>();
        test.add("hello");
        test.add("world");
        String[] array = new String[test.size()];
        String[] testarray = test.toArray(array);
        System.out.println(Arrays.toString(testarray));
    }
    public static void testMethod(String s){
        System.out.println(s.length());
    }
}
