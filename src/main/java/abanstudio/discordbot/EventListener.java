/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;

/**
 *
 * @author General
 */
public class EventListener {
    
    
    HashMap<Class, ArrayList<MethodTuple>> methods;
    
    public EventListener(){
        methods = new HashMap<>();
    }
     
    @EventSubscriber
    public void onEvent(Event event){
        System.out.println("Received event "+ event);
        ArrayList<MethodTuple> mList = methods.get(event.getClass());
        if(mList == null)
            return;
        
        
        mList.stream().forEach((tuple) -> {
            try {            
                tuple.getObject().getClass();
                tuple.getMethod().invoke(tuple.getObject(), event);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }
    
    public HashMap<Class, ArrayList<MethodTuple>> getMethodMap(){return methods;}
    
}
