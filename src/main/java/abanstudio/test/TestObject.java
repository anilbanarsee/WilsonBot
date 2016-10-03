/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.test;

/**
 *
 * @author User
 */
public class TestObject {
    
    String message;
    
    public TestObject(String m){
        message = m;
    }
    
    public void setMessage(String m){
        message = m;
    }
    public String toString(){
        return message;
    }
}
