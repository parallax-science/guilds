package parallaxscience.guilds.utility;

import java.io.File;

public class FileUtility
{
	public static final String guildDirectory = "world/guilds";

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void directoryCheck()
	{
		new File(guildDirectory).mkdir();
	}
}
