/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.IOException;
import java.util.List;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

/**
 *
 * @author Reetoo
 */
public class YTApi {
    public static void main(String[] args) throws IOException{
        System.out.println(getLength("OaozqqVAujY"));
    }
    public static long getLength(String video) throws IOException{
        YouTube  youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-length-sample").build();
        YouTube.Videos.List listVideosRequest = youtube.videos().list("contentDetails").setId(video);

          listVideosRequest.setKey("AIzaSyB3zdVWRersVlpld_3vbIsNNhM80JKnPdM");
          VideoListResponse listResponse = listVideosRequest.execute();
          List<Video> videoList = listResponse.getItems();
          String period = videoList.get(0).getContentDetails().getDuration();
        
        
        PeriodFormatter formatter = ISOPeriodFormat.standard();
        Period p = formatter.parsePeriod(period);
        Minutes m = p.toStandardMinutes();

     //   System.out.println(m.getMinutes());
        return m.getMinutes();
    }
        public static long getLengthS(String video) throws IOException{
        YouTube  youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-length-sample").build();
        YouTube.Videos.List listVideosRequest = youtube.videos().list("contentDetails").setId(video);

          listVideosRequest.setKey("AIzaSyB3zdVWRersVlpld_3vbIsNNhM80JKnPdM");
          VideoListResponse listResponse = listVideosRequest.execute();
          List<Video> videoList = listResponse.getItems();
          String period = videoList.get(0).getContentDetails().getDuration();
        
        
        PeriodFormatter formatter = ISOPeriodFormat.standard();
        Period p = formatter.parsePeriod(period);
        Seconds  s = p.toStandardSeconds();

     //   System.out.println(m.getMinutes());
        return s.getSeconds();
    }
            

}
