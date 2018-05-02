/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.command;

import abanstudio.command.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Links metadata with an action. Metadata includes regex, regex string that triggers the command from user-side; comm,
 * the absolute name of the command (must be unique from all other commands including module commands on a server);
 * des_sh, a short description of the command used when listing a large number of commands; desc, full description of
 * the command including an example usage.
 *
 * @author Anil James Banarsee
 */
public class Command
{


	private Action action;
	CommandData info;
	int adminLevel;
	private List<Command> subcommands;
	private Command parent;


	public Command(Action a, CommandData info, Command parent)
	{
		setAction(a);
		this.info = info;
		this.parent = parent;
		adminLevel = 0;
		subcommands = new ArrayList<>();
	}
	public CommandData getInfo(){
		return info;
	}
	/*
	Attempts to add a command as a subcommand, if a subcommand with a matching identifier exists, return it, otherwise
	add the command and return null
	 */
	public Command addSubCommand(Command command){

		for(Command c: subcommands){
			if(c.getInfo().getComm().equals(command.getInfo().getComm())){
				return c;
			}
		}
		subcommands.add(command);
		return null;
	}
	/*
	public final void setInfo(String[] info)
	{
		regex = "";
		comm = "";
		desc_sh = "";
		desc = "";
		if (info.length > 0)
			regex = info[NREGEX];
		if (info.length > 1)
			comm = info[NCOMM];
		if (info.length > 2)
			desc_sh = info[NDESCS];
		if (info.length > 3)
			desc = info[NDESCL];

	}
	*/

	public final void setAction(Action a)
	{
		action = a;
	}
	public final void setCommandData(CommandData cd){
		info = cd;
	}
	public Action getAction()
	{
		return action;
	}

	public Command getParent(){
		return parent;
	}

	public Command getSubCommand(String s){
		for(Command c: subcommands){
			if(c.getInfo().getComm().equals(s)){
				return c;
			}
		}
		return null;
	}
	public static Command matchCommand(ArrayList<Command> commList, String input)
	{
		for (Command c : commList) {
			Pattern p = Pattern.compile(c.getInfo().getRegex());
			Matcher m = p.matcher(input);

			if (m.matches()) {
				return c;
			}
		}
		return null;
	}

	public int getAdminLevel()
	{
		return adminLevel;
	}


}
