package parallaxscience.guilds.utility;

import java.io.File;

public class FileUtility
{
	/**
	 * Guild mod save directory
	 */
	public static final String guildDirectory = "world/guilds";

	/**
	 * Checks to see if guild directory has been created
	 * If not, creates the directory
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void directoryCheck()
	{
		new File(guildDirectory).mkdir();
	}
}
