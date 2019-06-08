package parallaxscience.guilds.config;

import net.minecraftforge.common.config.Config;
import parallaxscience.guilds.Guilds;

/**
 * Interface for the guild section of the config file
 * @see Config
 * @author Tristan Jay
 */
@Config(modid = Guilds.MODID, category = "guild")
public class GuildConfig {

    /**
     * The amount of land a guild can claim per member
     */
    @Config.Comment({
            "The amount of land a guild can claim per member",
            "Default: 50"
    })
    @Config.Name("Number of Claims Per Member")
    public static int claimPerPlayer = 50;
}
