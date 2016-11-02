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
    
    public Action(Action[] cs){
        subComms = cs;
    }
    public Action(){
        subComms = null;
    }
    
    public abstract void exec(String[] arguments, IMessage message);
    
}
