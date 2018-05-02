/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.utils;


import java.io.File;
import java.io.IOException;

/**
 * @author Reetoo
 */
public class YTDownloader
{


	public static File download(String url) throws IOException, InterruptedException
	{
		String filename;
		final Process p = Runtime.getRuntime().exec("youtube-dl -o assets/downloaded/%(id)s.%(ext)s " + url);

		ReadingThread rt = new ReadingThread(p);
		rt.start();
		p.waitFor();
		for (File f : new File("assets/downloaded/").listFiles()) {
			if (f.getName().split("\\.")[0].equals(rt.filename.split("\\.")[0])) {
				return new File("assets/downloaded/" + f.getName());
			}
		}

		return null;

	}

}
