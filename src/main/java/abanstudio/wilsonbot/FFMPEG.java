/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

/**
 *
 * @author Reetoo
 */
public class FFMPEG {
    
    String mainPath;
    
    public FFMPEG(String path){
        mainPath= path;
    }
    
    public void convertAndTrim(File f, String name, int[] times, String subpath){
        File file = new File("assets/downloaded/"+f.getName().split("\\.")[0]+".webm");
        if(file.exists()){
            System.out.println("webm exists, using that");
            f = file;
        }
        FFmpeg ffmpeg = null;
        try {
           // ffmpeg = new FFmpeg("C:\\Users\\Reetoo\\Documents\\ffmpeg\\bin\\ffmpeg.exe");
            ffmpeg = new FFmpeg(mainPath+"ffmpeg\\bin\\ffmpeg.exe");
        } catch (IOException ex) {
            System.out.println("Error in starting FFmpeg object");
            return;
        }
        //FFprobe ffprobe = new FFprobe("C:\\Users\\Reetoo\\Documents\\ffmpeg\\bin\\ffprobe.exe");
          FFprobe ffprobe = new FFprobe(mainPath+"ffmpeg\\bin\\ffprobe.exe");     
        FFmpegOutputBuilder oBuilder = new FFmpegBuilder()

            .setInput(mainPath+"/assets/downloaded/"+f.getName())     // Filename, or a FFmpegProbeResult
            .overrideOutputFiles(true) // Override the output if it exists
            .addOutput(mainPath+"/assets/"+subpath+name+".mp3")   // Filename for the destination
            .setFormat("mp3")
                .setAudioBitRate(65536)

                ;
        
        FFmpegBuilder builder;
        if(times==null){
            
            builder = oBuilder.done();
        }
        else if(times.length==0){
            builder = oBuilder.done();
        }
        else if(times.length==1){
            builder = oBuilder.setDuration(times[0], TimeUnit.SECONDS).done();
        }
        else{
            builder = oBuilder.setStartOffset(times[0], TimeUnit.SECONDS).setDuration(times[1], TimeUnit.SECONDS).done();
        }
        
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run a one-pass encode
        executor.createJob(builder).run();
        System.out.println("start job 111");
        

    }
    public static void convert(File f){
        
    }
}
