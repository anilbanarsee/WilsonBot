/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import abanstudio.games.chameleon.ChameleonGame;
import abanstudio.utils.FFMPEG;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.module.Admin;
import abanstudio.module.Games;
import abanstudio.module.InvalidGameClassException;
import abanstudio.module.Soundboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 * @author Reetoo
 */
public class Main
{
	public static FFMPEG ffmpeg;
	public static IDiscordClient wilsonClient;
	static ArrayList<IUser> users;

	public static void main(String[] args) throws DiscordException, IOException
	{

		users = new ArrayList<>();

		System.out.println("Initalizing ffmpeg");

		String path = "";


		File f = new File("");
		path = f.getAbsolutePath();

		ffmpeg = new FFMPEG(path);

		System.out.println("Connecting");
		try (FileInputStream inputStream = new FileInputStream("wilsontokentest.txt")) {
			//System.out.println("Token: "+IOUtils.toString(inputStream));
			//System.out.println("Token: "+IOUtils.toString(inputStream));
			wilsonClient = new ClientBuilder().withToken(IOUtils.toString(inputStream, Charset.defaultCharset())).login();
		} catch (FileNotFoundException e) {
			System.out.println("wilsontoken.txt not found. If you are running this for the first time, you must obtain a discord bot token and insert it into a file named as such.");
		}


		WilsonServer wilson = new WilsonServer(wilsonClient);

		System.out.println("Loading modules ...");
		wilson.addModule(new Admin(wilson));
		wilson.addModule(new Soundboard(wilson));

		Games games = new Games(wilson);
		wilson.addModule(games);
		games.addGameClass("chameleon", ChameleonGame.class);

		//wilson.addModule(new Games());
		// djdogClient.getDispatcher().registerListener(djdog);
		// wilsonClient.getDispatcher().registerListener(wilson);


	}
}
