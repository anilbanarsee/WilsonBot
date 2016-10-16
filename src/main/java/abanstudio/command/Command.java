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
public abstract class Command {
    
    private Command[] subComms;
    
    public Command(Command[] cs){
        subComms = cs;
    }
    public Command(){
        subComms = null;
    }
    
    public abstract void exec(String[] arguments, IMessage message);
    
}
