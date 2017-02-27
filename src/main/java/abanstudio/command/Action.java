/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.command;

import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Reetoo
 */
public abstract class Action {
    
    private Action[] subComms;
    private Object origin;
    
    public Action(Action[] cs){
        this();
        subComms = cs;
        
    }
    public Action(){
        subComms = null;
        origin = null;
    }
    public void setOrigin(Object o){
        origin = o;
    }
    public Object getOrigin(){return origin;}
    public abstract void exec(String[] arguments, IMessage message);
    
}
