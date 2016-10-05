/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import java.io.File;

/**
 *
 * @author User
 */
public class Clip {
    
    File file;
    float volume;
    
    public Clip(File f, float volume){
        file = f;
        this.volume = volume;
    }
    
    public File getFile(){
        return file;
    }
    
    public float getVolume(){ return volume; }
    
}
