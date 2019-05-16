package parallaxscience.guilds.raid;

import parallaxscience.guilds.guild.GuildCache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidCache {

    private final static String fileName = "world/Guilds_RaidsCache.dat";

    private static HashMap<String, Raid> raids;

    public static void initialize()
    {
        raids = new HashMap<>();
    }

    public static Raid getPlayerRaid(UUID player)
    {
        for(Map.Entry<String, Raid> raidEntry : raids.entrySet())
        {

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
    }

    public static void stopRaid(String raid)
    {
        raids.remove(raid);
    }
}
