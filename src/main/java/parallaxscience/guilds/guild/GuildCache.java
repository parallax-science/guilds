package parallaxscience.guilds.guild;

import parallaxscience.guilds.Guilds;
import java.io.*;
import java.util.*;

/**
 * Class that is used to store and manage guild information
 * Holds the master list of guilds
 * @author Tristan Jay
 */
public final class GuildCache {

    /**
     * Filepath to the GuildCache save file location
     */
    private final static String fileName = "world/Guilds_GuildCache.dat";

    /**
     * HashMap of guilds
     * Key is the name of the guild
     * @see HashMap
     * @see Guild
     */
    private static HashMap<String, Guild> guilds;

    /**
     * Initialize function for the class
     * Attempts to load the guild data from file
     * If no guild data is found, create a new HashMap
     */
    @SuppressWarnings("unchecked")
    public static void initialize()
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            guilds = (HashMap<String, Guild>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch(Exception e)
        {
            guilds = new HashMap<>();
        }
    }

    /**
     * Returns the guild of a specified player
     * @param player UUID of player
     * @return Guild object reference
     */
    public static Guild getPlayerGuild(UUID player)
    {
        for(Map.Entry<String, Guild> g : guilds.entrySet()) if(g.getValue().isMember(player)) return g.getValue();
        return null;
    }

    /**
     * Returns the guild object reference associated with the guild name
     * @param guildName String name of the guild
     * @return Guild object reference
     */
    public static Guild getGuild(String guildName)
    {
        return guilds.get(guildName);
    }

    /**
     * Returns a list of all of the guilds
     * @return String List of all guilds
     */
    public static List<String> getGuildList()
    {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Guild> entry : guilds.entrySet())
        {
            list.add(entry.getKey());
        }
        return list;
    }

    /**
     * Returns a list of all guilds who are not currently a part of an alliance
     * @return String List of the guilds not in an alliance
     */
    public static List<String> getFreeGuilds()
    {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Guild> entry : guilds.entrySet())
        {
            if(entry.getValue().getAlliance() == null) list.add(entry.getKey());
        }
        return list;
    }

    /**
     * Adds a new guild to the guild list
     * @param guildName String name of the guild
     * @param guildMaster UUID of the guild master
     * @return true if the guild doesn't already exist
     */
    public static boolean addGuild(String guildName, UUID guildMaster)
    {
        if(guilds.containsKey(guildName)) return false;
        guilds.put(guildName, new Guild(guildName, guildMaster));
        return true;
    }

    /**
     * Removes a guild from the guild list
     * @param guild reference to the Guild object
     */
    public static void removeGuild(Guild guild)
    {
        ChunkCache.removeAllClaimed(guild.getGuildName());
        guilds.remove(guild.getGuildName());
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
            objectOutputStream.writeObject(guilds);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {
            Guilds.logger.info("ERROR: IOException on guild cache file save");
        }
    }
}
