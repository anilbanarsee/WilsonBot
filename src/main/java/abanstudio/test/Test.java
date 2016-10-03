/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

import abanstudio.utils.sqlite.DBHandler;
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
            
        Random r = new Random();
        
        ArrayList<Integer> list = new ArrayList<>();
        
        for(int n = 0; n<600; n++)
            list.add(r.nextInt(6));
        
        int x = 0;
        
        for(int i = 0; i<list.size(); i++){
            if(list.get(i)==5){
                
                x++;
            }
        }
        System.out.println(x);
        
    }
}
