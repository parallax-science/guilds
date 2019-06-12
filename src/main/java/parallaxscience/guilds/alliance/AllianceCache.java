package parallaxscience.guilds.alliance;

import parallaxscience.guilds.Guilds;
import parallaxscience.guilds.guild.Guild;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that is used to store and manage alliance information
 * Holds the master list of alliances
 * @author Tristan Jay
 */
public class AllianceCache {

    /**
     * Filepath to the AllianceCache save file location
     */
    private final static String fileName = "world/Guilds_AllianceCache.dat";

    /**
     * List of all of the alliances
     * @see HashMap
     */
    private static HashMap<String, Alliance> alliances;

    /**
     * Initialize function for the class
     * Attempts to load the alliance data from file
     * If no alliance data is found, create a new HashMap
     */
    @SuppressWarnings("unchecked")
    public static void initialize()
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            alliances = (HashMap<String, Alliance>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch(Exception e)
        {
            alliances = new HashMap<>();
        }
    }

    /**
     * Creates a new alliance
     * @param alliance String name of the alliance
     * @param guildName String name of the guild
     */
    public static void createAlliance(String alliance, String guildName)
    {
        alliances.put(alliance, new Alliance(guildName));
    }

    /**
     * Removes a guild from an alliance
     * If the guild is the last guild, removes the alliance from the alliance list
     * @param guild Guild object reference
     */
    public static void leaveAlliance(Guild guild)
    {
        String guildName = guild.getGuildName();
        Alliance alliance = getAlliance(guildName);
        if(alliance != null)
        {
            alliance.removeGuild(guild.getGuildName());
            if(alliance.getGuildCount() == 0) alliances.remove(guildName);
        }
        guild.setAlliance(null);
    }

    /**
     * Returns the object reference of an alliance
     * @param allianceName String name of the alliance
     * @return Alliance object reference
     */
    public static Alliance getAlliance(String allianceName)
    {
        return alliances.get(allianceName);
    }

    /**
     * Returns a list of alliances
     * @return List String of all alliances
     */
    public static List<String> getAllianceList()
    {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Alliance> entry : alliances.entrySet())
        {
            list.add(entry.getKey());
        }
        return list;
    }

    /**
     * Saves the guild data to file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save()
    {
        File file = new File(fileName);
        try
        {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(alliances);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {
            Guilds.logger.info("ERROR: IOException on alliance cache file save");
        }
    }
}
