/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.games.countdown;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author General
 */
public class CalcTreeTrunk
{

	int[] nums;
	CalcTree[] subNodes;
	int target;
	public HashMap<Double, ArrayList<CalcTree>> currentVals;

	public CalcTreeTrunk(int[] start, int target)
	{
		subNodes = new CalcTree[start.length];
		currentVals = new HashMap<>();
		nums = start;
		this.target = target;
	}

	public boolean checkVal(CalcTree tree)
	{
		ArrayList<CalcTree> list = currentVals.get(tree.value);
		if (list == null) {
			list = new ArrayList<>();
			list.add(tree);
			currentVals.put(tree.value, list);
			return true;
		} else {

			for (CalcTree t : list) {
				//int[] left = tree.remaining;
				for (int x : tree.remaining) {
					boolean flag = false;
					for (int y : t.remaining) {
						if (x == y) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						list.add(tree);
						return true;
					}
				}
			}
			return false;
		}
	}

	public void expandStart(String arg)
	{
		int x = 0;
		int[] newNum = new int[nums.length - 1];

		for (int i = 0; i < nums.length; i++) {
			int y = 0;

			for (int j = 0; j < nums.length; j++) {

				if (i != j) {
					newNum[y] = nums[j];
					y++;
				}


			}
			subNodes[i] = new CalcTree(newNum, nums[i], this);
			if (arg.equals("DFS"))
				subNodes[i].expandDFS();
			else
				subNodes[i].expandSingle();

		}
	}
}
