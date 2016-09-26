/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

/**
 *
 * @author Reetoo
 */
import java.io.IOException;
 
public class TestDownloader {
 
    public static void main(String[] args) throws InvalidDownloadException {
        String fileURL = "http://i.4cdn.org/wsg/1463718592250.webm";
        String saveDir = "assets/downloaded";
        try {
            FileDownloader.downloadFile(fileURL, saveDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}