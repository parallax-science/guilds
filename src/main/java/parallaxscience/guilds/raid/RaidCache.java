package parallaxscience.guilds.raid;

import net.minecraftforge.common.MinecraftForge;
import parallaxscience.guilds.events.RaidEvents;
import parallaxscience.guilds.guild.GuildCache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidCache {

    private final static String fileName = "world/Guilds_RaidsCache.dat";

    private static HashMap<String, Raid> raids;

    private static RaidEvents raidEvents;
    private static boolean isActive;

    public static void initialize()
    {
        raids = new HashMap<>();
        raidEvents = new RaidEvents();
        //Perform mass raid restore
    }

    public static Raid getPlayerRaid(UUID player)
    {
        for(Map.Entry<String, Raid> raidEntry : raids.entrySet())
        {
            //Implement
        }
        return null;
    }

    public static Raid getRaid(String raidName)
    {
        return raids.get(raidName);
    }

    public static void createRaid(String raidName, UUID primaryAttacker)
    {
        raids.put(raidName, new Raid(raidName, primaryAttacker));
        if(!isActive) MinecraftForge.EVENT_BUS.register(raidEvents);
    }

    public static void stopRaid(String raid, boolean defenseWon)
    {
        //Declaration
        //Chunk restore
        //Unregister raid timer
        raids.remove(raid);
        if(raids.isEmpty())
        {
            MinecraftForge.EVENT_BUS.unregister(raidEvents);
            isActive = false;
        }
    }

    public static void cancelRaid(String raid)
    {
        raids.remove(raid);
    }
}
