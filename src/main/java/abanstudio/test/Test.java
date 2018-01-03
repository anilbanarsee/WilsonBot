/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.module.Games;
import abanstudio.utils.sqlite.DBHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException, IOException{
        
        MessageReceivedEvent event;
        ReadyEvent event2;
        
        HashMap<Class, String> map = new HashMap<>();
        
        map.put(MessageReceivedEvent.class, "apple");
        map.put(ReadyEvent.class, "banana");
        
        System.out.println(map.get(MessageReceivedEvent.class));
        System.out.println(map.get(ReadyEvent.class));
        System.out.println(map.get(Event.class));
        System.out.println(map.get(ChannelCreateEvent.class));
        
        
       
        //System.out.println(methods);
    }
    public static void testMethod(String s){
        System.out.println(s.length());
    }
}
