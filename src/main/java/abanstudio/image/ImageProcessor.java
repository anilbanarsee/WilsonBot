/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Reetoo
 */
public class ImageProcessor {
    public static void setSilhouette(BufferedImage image, int alphaThresh){

        //System.out.println(image.getType());
        for(int y = 0; y<image.getHeight(); y++){
            for(int x=0; x<image.getWidth(); x++){
            
                Color c = new Color(image.getRGB(x, y));
                 int pixel = image.getRGB(x,y);
                int alpha = 0xFF & (pixel >> 24);
                
                 
  if( alpha > 200 ) {
      image.setRGB(x,y,Color.BLACK.getRGB());
  }
  else{
      image.setRGB(x, y, new Color(255,255,255,0).getRGB());
  }

             //   System.out.println(alpha);
               // if(alpha>alphaThresh)
                    //image.setRGB(x, y, 0);
                //else
                  //  image.setRGB(x, y, 1);
                
            }
        }
        
    }
    
    public static void main(String[] args){
    
       File folder = new File("assets/pokemon/artwork");
       
       for(File f : folder.listFiles()){
           try {
               BufferedImage b = ImageIO.read(f);
               setSilhouette(b,200);
               ImageIO.write(b, "png", new File("assets/pokemon/sil/"+f.getName()));
           } catch (IOException ex) {
               Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
           }
           
       }
        
        
    }
}
