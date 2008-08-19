package at.morphic.apps.musicplayer;

import java.util.Vector;
import java.io.*;

/*
 Abstraction to execute an applescript 
 this class is highly based upon the interface of the NSApplescript,
 however as that class is so terrible slow this is a better alternative.
 */
public class AppleScript {
	private String script;
	private Vector result;

	public AppleScript(String script) {
		this.script = script;
		result = new Vector();
	}

	/*
	   Executes the script
	   It first writes the script to a file,
	   that is not strictly needed as we could brake up the script command 
	   and use the -e flag of osascript.
	   If you feel like the one to change this feel free to do so :)
	*/
	public Vector execute() {
		result.removeAllElements();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("script_tmp"));
			out.write(this.script);
			out.close();
			
			Process process = Runtime.getRuntime().exec("osascript script_tmp");
		    DataInputStream	reader = new DataInputStream(process.getInputStream());
			process.waitFor();

			String line;
			 while ((line = reader.readLine()) != null) {
			        System.out.println(line);
					result.add(line);
			 }
			return result;
		}
		catch (Exception e) {
			return result;
		}
	}

	public Vector getResult() {
		return result;
	}
}