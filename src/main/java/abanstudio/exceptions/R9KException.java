/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.exceptions;

/**
 *
 * @author Reetoo
 */
public class R9KException extends Exception{

    private long diff;
    private String name;
    
    public R9KException(long diff, String name){
        this.diff = diff;
        this.name = name;
    }
    
    public long getDiff(){
        return diff;
    }
    
    public String getName(){
        return name;
    }
}
