package parallaxscience.guilds;


import parallaxscience.guilds.guild.Guild;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public final class GuildCache {

    private final static String fileName = "world/Guilds_GuildCache.dat";

    private static HashMap<String, Guild> guilds;

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
     *
     * @param player
     * @return
     */
    public static Guild getPlayerGuild(UUID player)
    {
        for(Map.Entry<String, Guild> g : guilds.entrySet()) if(g.getValue().isMember(player)) return g.getValue();
        return null;
    }

    /**
     *
     * @param guildName
     * @return
     */
    public static Guild getGuild(String guildName)
    {
        return guilds.get(guildName);
    }

    /**
     *
     * @param guildName
     * @param guildMaster
     * @return
     */
    public static boolean addGuild(String guildName, UUID guildMaster)
    {
        if(guilds.containsKey(guildName)) return false;
        guilds.put(guildName, new Guild(guildName, guildMaster));
        return true;
    }

    /**
     *
     * @param guild
     */
    public static void removeGuild(Guild guild)
    {
        ChunkCache.removeAllClaimed(guild);
        guilds.remove(guild);
    }

    public static void save()
    {
        File file = new File(fileName);
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch(Exception e)
            {

            }
        }

        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(guilds);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {

        }
    }
}
