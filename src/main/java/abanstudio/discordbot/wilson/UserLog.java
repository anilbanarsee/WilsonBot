/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot.wilson;

import abanstudio.exceptions.R9KException;
import java.time.LocalTime;
import java.util.ArrayList;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.audio.AudioPlayer.Track;

/**
 *
 * @author Reetoo
 */
public class UserLog {
    
    ArrayList<Track> tracks;
    IUser user;
    private int maxClips = 5;
    private boolean r9k = true;
    private int r9kTime = 20;
    private int maxTime = 60;
    
    public UserLog(IUser user){
        this.user = user;
        tracks = new ArrayList<>();
    }
    
    public void addToLog(Track t){
        if(tracks.size()>=maxClips){
            tracks.remove(0);
        }
        tracks.add(t);
    }
    
    public long checkTrack(Track track) throws R9KException{
        LocalTime cTime = null;
        if(tracks.size()>=maxClips){
            Track first = tracks.get(0);
            cTime = (LocalTime) track.getMetadata().get("time");
            LocalTime fTime = (LocalTime) first.getMetadata().get("time");
            if(cTime.isBefore(fTime.plusSeconds(maxTime))){
                long diffSec = maxTime-(cTime.toSecondOfDay()-fTime.toSecondOfDay());
                return diffSec;
            }
            long numUn = currentUnplayed();
            if(numUn>=maxClips){
                return -numUn; 
            }
           
        }
         if(r9k){
             if(cTime==null)
                cTime = (LocalTime) track.getMetadata().get("time");
             for(Track t: tracks){

                LocalTime tTime = (LocalTime) t.getMetadata().get("time");
                    if(cTime.isBefore(tTime.plusSeconds(r9kTime))){
                        String trackName = (String) t.getMetadata().get("name");
                        if(trackName.equals(track.getMetadata().get("name"))){
                            long diffSec = r9kTime-(cTime.toSecondOfDay()-tTime.toSecondOfDay());
                            throw new R9KException(diffSec, trackName);
                        }
                    }
                }
            }
        addToLog(track);
        return 0;
    }
    
    public int currentUnplayed(){
        int n = 0;
        for(Track t: tracks){
            boolean b = (Boolean)t.getMetadata().get("played");
            if(!b)
                n++;
        }
        return n;
    }
    
    public ArrayList<Track> getTimes(){
        return tracks;
    }
    
}
