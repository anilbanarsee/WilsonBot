/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import abanstudio.utils.FFMPEG;
import abanstudio.discordbot.djdog.DjDogServer;
import abanstudio.discordbot.wilson.WilsonServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
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
        try(FileInputStream inputStream = new FileInputStream("wilsontoken.txt")) 
        {     
            //System.out.println("Token: "+IOUtils.toString(inputStream));
            //System.out.println("Token: "+IOUtils.toString(inputStream));
            wilsonClient = new ClientBuilder().withToken(IOUtils.toString(inputStream)).login();
            //wilsonClient = new ClientBuilder().withToken("MTgwOTczODE1OTQ0MTgzODA4.C4oMnw.W8oZYNfWcgLa-DHj9K4Rk1xs2p8").login();
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
        
        djdogClient.getDispatcher().registerListener(djdog);
        wilsonClient.getDispatcher().registerListener(new WilsonServer(wilsonClient,djdog));

        
    }
}
