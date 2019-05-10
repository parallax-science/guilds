package parallaxscience.guilds;


import parallaxscience.guilds.guild.Guild;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 */
public final class GuildCache {

    private final static String fileName = "world/Guilds_GuildCache.dat";

    private static ArrayList<Guild> guilds;

    public static void initialize()
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            guilds = (ArrayList<Guild>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch(Exception e)
        {
            guilds = new ArrayList<>();
        }
    }

    /**
     *
     * @param player
     * @return
     */
    public static Guild getPlayerGuild(UUID player)
    {
        for(Guild g : guilds) if(g.isMember(player)) return g;
        return null;
    }

    /**
     *
     * @param guildName
     * @param guildMaster
     * @return
     */
    public static boolean addGuild(String guildName, UUID guildMaster)
    {
        for(Guild g : guilds) if(g.toString().equals(guildName)) return false;
        guilds.add(new Guild(guildName, guildMaster));
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
