/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import abanstudio.djdog.DjDogServer;
import abanstudio.wilson.WilsonServerTest;
import java.io.File;
import java.util.ArrayList;
import sx.blah.discord.api.ClientBuilder;
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
    
    public static void main(String[] args) throws DiscordException{
       
        users = new ArrayList<>();
        
        System.out.println("Initalizing ffmpeg");
        
        String path = "";
        
            
        File f =  new File("");
        path = f.getAbsolutePath();
        
        ffmpeg = new FFMPEG(path);
        
        System.out.println("Connecting");
        wilsonClient = new ClientBuilder().withToken("MTgwOTczODE1OTQ0MTgzODA4.ChiAog.Z6vL_7Ws9fDurKT4DziLuzFQGmY").login();
        djdogClient = new ClientBuilder().withToken("MjMyODQyNDMzNTM5MzQyMzM3.CtUzGQ.ahwIgKZ2FOW1og_ZouciwBG8tSQ").login();
        
        DjDogServer djdog = new DjDogServer(djdogClient);
        
        djdogClient.getDispatcher().registerListener(djdog);
        wilsonClient.getDispatcher().registerListener(new WilsonServerTest(wilsonClient,djdog));
        
    }
}
