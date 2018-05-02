/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.module;

/**
 * @author Anil James Banarsee
 */
public class InvalidGameClassException extends Exception
{

	/**
	 * Creates a new instance of <code>InvalidGameClassException</code> without
	 * detail message.
	 */
	public InvalidGameClassException()
	{
	}

	/**
	 * Constructs an instance of <code>InvalidGameClassException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public InvalidGameClassException(String msg)
	{
		super(msg);
	}
}
