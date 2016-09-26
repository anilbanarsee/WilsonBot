/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import java.util.ArrayList;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author Reetoo
 */
public class Main {
    
    static IDiscordClient client;
    static ArrayList<IUser> users;
    
    public static void main(String[] args) throws DiscordException{
       
        users = new ArrayList<>();
        System.out.println("Connecting");
        client = new ClientBuilder().withToken("MTgwOTczODE1OTQ0MTgzODA4.ChiAog.Z6vL_7Ws9fDurKT4DziLuzFQGmY").login();
        
        client.getDispatcher().registerListener(new MainListener());
        
    }
}
