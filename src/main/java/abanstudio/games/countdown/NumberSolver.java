/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.countdown;

/**
 * @author General
 */
public class NumberSolver
{

	int[] numbers;

	public static void main(String[] args)
	{
		int[] nums = {6, 2, 7, 1, 9, 8};
		CalcTreeTrunk tree = new CalcTreeTrunk(nums, 234);
		tree.expandStart("DFS");
       /* for(CalcTree t: tree.subNodes){
            System.out.println("-----------");
            System.out.println(t);
            System.out.println("-----------");
            for(CalcTree st: t.subNodes){
                System.out.println(st);
            }
        }*/
	}

}
