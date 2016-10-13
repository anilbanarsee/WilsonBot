/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author Reetoo
 */
public class UserLog {
    
    ArrayList<LocalTime> times;
    IUser user;
    private int maxClips = 4;
    private int maxTime = 60;
    
    public UserLog(IUser user){
        this.user = user;
        times = new ArrayList<>();
    }
    
    public void addToLog(LocalTime d){
        if(times.size()>=maxClips){
            times.remove(0);
        }
        times.add(d);
    }
    
    public long checkTime(LocalTime d){
        if(times.size()>=maxClips){
            LocalTime first = times.get(0);
            if(d.isBefore(first.plusSeconds(maxTime))){
                long diffSec = maxTime-(d.toSecondOfDay()-first.toSecondOfDay());
                return diffSec;
            }
        }
        addToLog(d);
        return -1;
    }
    
    public ArrayList<LocalTime> getTimes(){
        return times;
    }
    
}
