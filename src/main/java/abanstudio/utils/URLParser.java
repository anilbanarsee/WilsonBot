/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Reetoo
 */
public class URLParser {
    public static void main(String[] args) throws MalformedURLException{
        System.out.println(checkURL("https://youtube.com/watch?v=HuDxgaHxelQ"));
        
    }
    public static boolean checkURL(String u) throws MalformedURLException{
        URL url = new URL(u);

        String host = url.getHost();

        String[] hosts = host.split("\\.");
        if(hosts.length>2)
            host = hosts[1];
        else
            host = hosts[0];
        
        
        System.out.println(host);
        if(!host.equals("youtube")){
            System.out.println("non-youtube videos are not accepted atm");
            return false;
        }
        return true;
    }
}
