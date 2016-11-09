/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot.wilson;

import java.util.ArrayList;

/**
 *
 * @author Reetoo
 */
public class GuildSettings {
    
    private boolean r9k_def, quiet;
    private String redirect, redirect_id;
    
    public GuildSettings(ArrayList<String> data){
        
      
        r9k_def = Boolean.parseBoolean(data.get(0));
        redirect = data.get(1);
        redirect_id = data.get(2);
        quiet = Boolean.parseBoolean(data.get(3));
        
    }
    
    public boolean getR9k_def(){return r9k_def;};
    public boolean getQuiet(){return quiet;};
    public String getRedirect(){return redirect;};
    public String getRedirect_Id(){return redirect_id;};
    
}
