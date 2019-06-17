package parallaxscience.guilds.config;

import net.minecraftforge.common.config.Config;
import parallaxscience.guilds.Guilds;

/**
 * Interface for the raid section of the config file
 * @see Config
 * @author Tristan Jay
 */
@Config(modid = Guilds.MODID, category = "raid")
public class RaidConfig
{
    /**
     * How long the preparation phase lasts before the raid starts (in seconds)
     */
    @Config.Comment({
            "How long the preparation phase lasts before the raid starts (in seconds)",
            "Default: 600"
    })
    @Config.Name("Preparation Phase Duration")
    public static int prepSeconds = 600;

    /**
     * The raid duration (in seconds)
     */
    @Config.Comment({
            "The raid duration (in seconds)",
            "Once the time is up, the defenders will win the raid",
            "Default: 900"
    })
    @Config.Name("Raid Duration")
    public static int raidSeconds = 900;

    /**
     * The raid shield duration (in minutes)
     * This is how long after a raid a guild is protected from being raided again
     */
    @Config.Comment({
            "How long after a raid a guild is protected from being raided again (in minutes)",
            "Default: 480"
    })
    @Config.Name("Raid Shield Duration")
    public static int shieldDuration = 480;
}
