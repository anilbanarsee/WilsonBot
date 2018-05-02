/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.command;

import sx.blah.discord.handle.obj.IMessage;

/**
 * Represents a single action. Contains a method (exec) which is called when this action is called via BotServer.
 * Both Module and BotServer use these for all command based actions.
 *
 * @author Anil James Banarsee
 * @version 1.0
 */
public abstract class Action
{

	private Object origin;
	private boolean override;

	public Action()
	{
		origin = null;
		override = false;
	}

	public void setOrigin(Object o)
	{
		origin = o;
	}

	public Object getOrigin()
	{
		return origin;
	}
	public Action setOverride(boolean b){
		override = b;
		return this;
	}
	public boolean getOverride(){
		return override;
	}

	public abstract void exec(String[] arguments, IMessage message);

}
