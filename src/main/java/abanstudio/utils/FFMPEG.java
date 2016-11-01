/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
    
    public void convertAndTrim(File f, String name, double[] times, String subpath) throws IOException, InterruptedException{
        File file = new File("assets/downloaded/"+f.getName().split("\\.")[0]+".webm");
        if(file.exists()){
            System.out.println("webm exists, using that");
            f = file;
        }

            
        FFmpeg ffmpeg = null;
        try {
           // ffmpeg = new FFmpeg("C:\\Users\\Reetoo\\Documents\\ffmpeg\\bin\\ffmpeg.exe");
            System.out.println(mainPath+"\\ffmpeg\\bin\\ffmpeg.exe");
            ffmpeg = new FFmpeg(mainPath+"\\ffmpeg\\bin\\ffmpeg.exe");
        } catch (IOException ex) {
            System.out.println("Error in starting FFmpeg object");
            return;
        }
        //FFprobe ffprobe = new FFprobe("C:\\Users\\Reetoo\\Documents\\ffmpeg\\bin\\ffprobe.exe");
          FFprobe ffprobe = new FFprobe(mainPath+"\\ffmpeg\\bin\\ffprobe.exe");     
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
            
            double du = ((times[0]-(times[0]%1))*1000)+(times[0]%1)*1000;
            long duration = Math.round(du);
            builder = oBuilder.setDuration(duration, TimeUnit.MILLISECONDS).done();   
        }
        else{
            double du = ((times[1]-(times[1]%1))*1000)+(times[1]%1)*1000;
            long duration = Math.round(du);
            double du2 = ((times[0]-(times[0]%1))*1000)+(times[0]%1)*1000;
            long startpoint = Math.round(du2);
            System.out.println("Starting at : "+startpoint+" with Duration :"+duration);
            builder = oBuilder.setStartOffset(startpoint, TimeUnit.MILLISECONDS).setDuration(duration, TimeUnit.MILLISECONDS).done();
        }
        
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run a one-pass encode
        executor.createJob(builder).run();
        System.out.println("start job 111");
     

    }
    public void convertAndTrim(File f, String name, String[] times, String subpath) throws IOException, InterruptedException{

            System.out.println("trimming");
            Process p;
            if(times.length == 0)
                p = Runtime.getRuntime().exec("ffmpeg -i assets/downloaded/"+f.getName()+" -codec copy assets/downloaded/temp"+f.getName());
   
    
            else if(times.length == 1)
               p = Runtime.getRuntime().exec("ffmpeg -i assets/downloaded/"+f.getName()+" -codec copy -t "+times[0]+" assets/downloaded/temp"+f.getName());
   
            else{
               
               
               p = Runtime.getRuntime().exec("ffmpeg -i assets/downloaded/"+f.getName()+" -ss "+times[0]+" -codec copy -t "+times[1]+" assets/downloaded/temp"+f.getName());
            }
            ReadingThread rt = new ReadingThread(p);
            rt.start();
            p.waitFor();
            
            System.out.println("cutting");
            p = Runtime.getRuntime().exec("ffmpeg -i assets/downloaded/temp"+f.getName()+" -vn -ab 256 assets/"+subpath+name+".mp3");
            
            rt = new ReadingThread(p);
            rt.start();
            p.waitFor();
            System.out.println("deleting temp");
            p = Runtime.getRuntime().exec("rm assets/downloaded/temp"+f.getName());
            
            rt = new ReadingThread(p);
            rt.start();
            p.waitFor();

    }
    public static void convert(File f){
        
    }
}
