/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.utils.sqlite.DBHandler;
import abanstudio.wilsonbot.FFMPEG;
<<<<<<< HEAD
import abanstudio.wilsonbot.UserLog;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
=======
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
>>>>>>> 031a50c2467d728cbc926fe4df6b7592c17d72f8
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.FileProvider;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException{


        UserLog user = new UserLog(null);
        

        while(true){
            Scanner s = new Scanner(System.in);
            String line = s.next();
            if(line.equals("end"))
                break;

            System.out.println(user.checkTime(LocalTime.now()));
        
        }

    }
    
}
