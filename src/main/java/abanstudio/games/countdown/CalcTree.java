/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.countdown;

/**
 *
 * @author General
 */
public class CalcTree {
    
    CalcTree[] subNodes;
    char operator;
    int[] remaining;
    String seq;
    double value;
    CalcTreeTrunk trunk;
    

   public CalcTree(int[] rem, int val, CalcTreeTrunk trunk){
        subNodes = new CalcTree[rem.length*6];
        this.trunk = trunk;
        remaining = rem;
        value = val+0.0;
        seq = ""+val;
    }
    public CalcTree(int[] rem, double val, CalcTreeTrunk trunk){
        subNodes = new CalcTree[rem.length*6];
        remaining = rem;
        this.trunk = trunk;
        value = val;
        seq = ""+val;
        
    }
    
    public CalcTree(int[] rem, double val, String seq, CalcTreeTrunk trunk){
        this(rem,val,trunk);
        this.seq = seq;
        //operator = op;
        
    }
    
    public void expandDFS(){
        if(remaining.length<1){
            //System.out.println(trunk==null);
            if(trunk!=null){
                
                if(trunk.target==value){
                    //if(trunk.checkVal(this)){
                    System.out.println(this);
                    //}
                }
               
            }
            //System.out.println("hello");
        }
        int nodeIndex = 0;
        for(int i = 0; i<remaining.length; i++){
            
            
            int[] newRem = new int[remaining.length-1];
            int x = 0;
            for(int j=0; j<remaining.length; j++){
                if(j!=i){
                    newRem[x]=remaining[j];
                    x++;
                    
                }
            }
            double newVal = value+remaining[i];
            if(newVal!=value){
                if(trunk.checkVal(this)){
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'+'+remaining[i],trunk);
                    subNodes[nodeIndex].expandDFS();
                    nodeIndex++;
                }
            }
                    newVal = value-remaining[i];
                     if(newVal!=value){
                         if(trunk.checkVal(this)){
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'-'+remaining[i],trunk);
                    subNodes[nodeIndex].expandDFS();
                    nodeIndex++;
                         }
                     }
                    newVal = remaining[i]-value;
                     if(newVal!=value){
                         if(trunk.checkVal(this)){
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+remaining[i]+")"+'-'+seq,trunk);
                    subNodes[nodeIndex].expandDFS();
                    nodeIndex++;
                         }
                     }
                    newVal = value*remaining[i];
                     if(newVal!=value){
                         if(trunk.checkVal(this)){
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'*'+remaining[i],trunk);
                    subNodes[nodeIndex].expandDFS();
                    nodeIndex++;
                         }
                     }
                    newVal = value/remaining[i];
                     if(newVal!=value){
                         if(trunk.checkVal(this)){
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'/'+remaining[i],trunk);
                    subNodes[nodeIndex].expandDFS();
                    nodeIndex++;
                         }
                     }
                    newVal = remaining[i]/value;
                     if(newVal!=value){
                         if(trunk.checkVal(this)){
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+remaining[i]+")"+'/'+seq,trunk);
                    subNodes[nodeIndex].expandDFS();
                    nodeIndex++;
                         }
                     }
        }
    }
    public void expandSingle(){
        if(remaining.length<1){
            return;
        }
        int nodeIndex = 0;
        for(int i = 0; i<remaining.length; i++){
            
            
            int[] newRem = new int[remaining.length-1];
            int x = 0;
            for(int j=0; j<remaining.length; j++){
                if(j!=i){
                    newRem[x]=remaining[j];
                    x++;
                    double newVal = value+remaining[j];
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'+'+remaining[j],trunk);
                    nodeIndex++;
                    newVal = value-remaining[j];
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'-'+remaining[j],trunk);
                    nodeIndex++;
                    newVal = remaining[j]-value;
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+remaining[j]+")"+'-'+seq,trunk);
                    nodeIndex++;
                    newVal = value*remaining[j];
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'*'+remaining[j],trunk);
                    nodeIndex++;
                    newVal = value/remaining[j];
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+seq+")"+'/'+remaining[j],trunk);
                    nodeIndex++;
                    newVal = remaining[j]/value;
                    subNodes[nodeIndex] = new CalcTree(newRem,newVal,"("+remaining[j]+")"+'/'+seq,trunk);
                    nodeIndex++;
                }
            }
        }
    }
    @Override
    public String toString(){
        return value+"{"+seq+"}";
    }
    
}
