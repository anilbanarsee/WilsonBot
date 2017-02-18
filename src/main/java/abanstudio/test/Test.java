/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.utils.sqlite.DBHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException, IOException{

        Object[] array = new Object[5];
        for(int i=1; i<6;i++){
            array[i-1] = i;
        }
        System.out.println(Arrays.toString(array));
    }
    public static void testMethod(String s){
        System.out.println(s.length());
    }
}
