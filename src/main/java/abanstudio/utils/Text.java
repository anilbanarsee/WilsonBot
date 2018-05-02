package abanstudio.utils;


import org.apache.commons.lang3.StringUtils;

/*
Class that contains methods pertaining to manipulation and formatting of text for use in messages
 */
public class Text
{
	/*
	Generates a table in text format for the given data and column heads
	 */
	public static String makeTable(String[] head, String[][] data){
		String div = "|";//String to use as column divider

		//Calculate minimum widths for each column O(n)
		int[] pad = new int[head.length];
		//Start by setting min width to column head length
		for(int i=0; i<head.length; i++){
			pad[i] = head[i].length();
		}
		//Now check if any values are greater for respective column
		for(String[] line: data){
			for(int i=0; i<line.length; i++){
				int n = line[i].length();
				if(n>pad[i]){
					pad[i] = n+2;
				}
			}
		}
		//Start with column headers
		String s = div;
		for(int i = 0; i<head.length; i++){
			s = s + StringUtils.center(head[i],pad[i])+div;//Centre the column based on pad
		}
		s = s+"\n";
		//Add data
		for(int i = 0; i<data.length; i++){
			s = s+div;
			for(int j = 0; j<data[i].length; j++){
				s = s+StringUtils.center(data[i][j],pad[j])+div;
			}
			s = s+"\n";
		}
		return s;



	}
}
