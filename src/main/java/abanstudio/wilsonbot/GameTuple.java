/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

/**
 *
 * @author User
 */
public class GameTuple {
    
    Game game;
    Thread thread;
    
    public GameTuple(){
        
    }
    public GameTuple(Game g, Thread t){
        setGame(g);
        setThread(t);
    }
    
    public Game getGame(){
        return game;
    }
    
    public Thread getThread(){
        return thread;
    }
    
    public void setGame(Game g){
        game = g;
    }
    
    public void setThread(Thread t){
        thread = t;
    }
    
    
}
