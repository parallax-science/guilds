package parallaxscience.guilds.utility;

import parallaxscience.guilds.Guilds;
import java.io.*;

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

	/**
	 * Reads an object from file
	 * @param fileName String name of file to be read
	 * @return Object read from file
	 */
	public static Object readFromFile(String fileName) throws IOException, ClassNotFoundException
	{
		FileInputStream fileInputStream = new FileInputStream(fileName);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		Object output = objectInputStream.readObject();
		objectInputStream.close();
		fileInputStream.close();
		return output;
	}

	/**
	 * Saves an object to file
	 * @param fileName String name of file to save to
	 * @param object Object to save to file
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void saveToFile(String fileName, Object object)
	{
		File file = new File(fileName);
		try
		{
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			fileOutputStream.close();
		}
		catch(Exception e)
		{
			Guilds.logger.info("ERROR: IOException while trying to save to " + fileName);
		}
	}
}
