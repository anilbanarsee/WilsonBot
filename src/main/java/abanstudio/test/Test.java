/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.utils.sqlite.DBHandler;
import abanstudio.wilsonbot.FFMPEG;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args){
         
        File f = new File("").getAbsoluteFile();
        
        
        FFMPEG ff = new FFMPEG(f.getAbsolutePath());
        ff.convertAndTrim(null, null, null, null);
    }
    
}
