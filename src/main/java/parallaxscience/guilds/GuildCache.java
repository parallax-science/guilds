package parallaxscience.guilds;

import parallaxscience.guilds.guild.Guild;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 */
public final class GuildCache {

    /**
     *
     */
    private static ArrayList<Guild> guilds;

    /**
     *
     */
    public static void initialize()
    {
        guilds = new ArrayList<>();
    }

    /**
     *
     */
    public static Guild getPlayerGuild(UUID player)
    {
        for(Guild g : guilds) if(g.isMember(player)) return g;
        return null;
    }

    /**
     *
     */
    public static boolean addGuild(String guildName, UUID guildMaster)
    {
        for(Guild g : guilds) if(g.toString().equals(guildName)) return false;
        guilds.add(new Guild(guildName, guildMaster));
        return true;
    }

    /**
     *
     */
    public static void removeGuild(Guild guild)
    {
        ChunkCache.removeAllClaimed(guild);
        guilds.remove(guild);
    }
}
