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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.codec.binary.Base64;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException, IOException{
        /*curl -i -H 'Accept: application/vnd.twitchtv.v2+json'\
-H 'Client-ID: axjhfp777tflhy0yjb5sftsil'\
'https://api.twitch.tv/kraken/channels/hebo'*/
        
        
   String stringUrl = "https://api.twitch.tv/kraken/channels/hebo";
        URL url = new URL(stringUrl);
        URLConnection uc = url.openConnection();

        uc.setRequestProperty("X-Requested-With", "Curl");

        String userpass = "username" + ":" + "password";
        String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
        uc.setRequestProperty("Authorization", basicAuth);

        InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
   
    }
    public static void testMethod(String s){
        System.out.println(s.length());
    }
}
