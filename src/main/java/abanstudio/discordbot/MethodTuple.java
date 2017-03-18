/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import java.lang.reflect.Method;

/**
 *
 * @author General
 */
public class MethodTuple {
    
    protected Method m;
    protected Object o;
    
    public MethodTuple(Method m,Object o){
        setMethodAndObject(m,o);
        
    }
    
    public Method getMethod(){return m;}
    public Object getObject(){return o;}
    
    public final void setMethodAndObject(Method m, Object o){
        this.m = m;
        this.o = o;
    }
   
    
}
