/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import abanstudio.utils.FFMPEG;
import abanstudio.discordbot.djdog.DjDogServer;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.module.Admin;
import abanstudio.module.Games;
import abanstudio.module.Soundboard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author Reetoo
 */
public class Main {
    public static FFMPEG ffmpeg;
    public static IDiscordClient wilsonClient;
    public static IDiscordClient djdogClient;
    static ArrayList<IUser> users;
    
    public static void main(String[] args) throws DiscordException, FileNotFoundException, IOException{
       
        users = new ArrayList<>();
        
        System.out.println("Initalizing ffmpeg");
        
        String path = "";
        
            
        File f =  new File("");
        path = f.getAbsolutePath();
        
        ffmpeg = new FFMPEG(path);
        
        System.out.println("Connecting");
        try(FileInputStream inputStream = new FileInputStream("wilsontokentest.txt")) 
        {     
            //System.out.println("Token: "+IOUtils.toString(inputStream));
            //System.out.println("Token: "+IOUtils.toString(inputStream));
            wilsonClient = new ClientBuilder().withToken(IOUtils.toString(inputStream)).login();
        }
        catch(FileNotFoundException e){
            System.out.println("wilsontoken.txt not found. If you are running this for the first time, you must obtain a discord bot token and insert it into a file named as such.");
        }

        try(FileInputStream inputStream = new FileInputStream("djtoken.txt")) 
        {     
            djdogClient = new ClientBuilder().withToken(IOUtils.toString(inputStream)).login();
            
        }
        catch(FileNotFoundException e){
            System.out.println("djtoken.txt not found. If you are running this for the first time, you must obtain a discord bot token and insert it into a file named as such.");

        }
        
        DjDogServer djdog = new DjDogServer(djdogClient);
        WilsonServer wilson = new WilsonServer(wilsonClient,djdog);
        
        System.out.println("Loading modules ...");
        wilson.addModule(new Admin(wilson));
        wilson.addModule(new Soundboard(wilson));
        
        //wilson.addModule(new Games());
        
        
       // djdogClient.getDispatcher().registerListener(djdog);
       // wilsonClient.getDispatcher().registerListener(wilson);
        
        
        
        
        
        

        
    }
}
