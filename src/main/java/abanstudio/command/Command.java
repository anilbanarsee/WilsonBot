/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.command;

import abanstudio.command.Action;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author General
 */
public class Command {
    

    private Action action;
    String regex, comm, desc_sh, desc;
    
    public Command(Action a){
        setAction(a);
        //info = new String[0];
        regex = "";
        comm = "";
        desc_sh = "";
        desc = "";
    }
    public Command(Action a, String[] info){
        setAction(a);
        setInfo(info);
    }

    public final void setInfo(String[] info){
        regex = "";
        comm = "";
        desc_sh = "";
        desc = "";
        if(info.length>0)
            regex = info[0];
        if(info.length>1)
            comm = info[1];
        if(info.length>2)
            desc_sh = info[2];
        if(info.length>3)
            desc = info[3];
    }
    public final void setAction(Action a){
        action = a;
    }
    public Action getAction(){
        return action;
    }

    public String[] getInfo(){
        String[] info = {regex,comm,desc_sh,desc};
        return info;
    }
    public String getRegex(){
        return regex;
    }
    public String getComm(){return comm;}
    public String getDescSh(){return desc_sh;}
    public String getDesc(){return desc;}
    
    public static Command matchCommand(ArrayList<Command> commList, String input){
        for(Command c : commList){
            Pattern p = Pattern.compile(c.getRegex());
            Matcher m = p.matcher(input);
            if(m.find()){
                return c;
            }
        }
        return null;
    }
    
    
}
