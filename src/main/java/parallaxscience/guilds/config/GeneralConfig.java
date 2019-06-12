package parallaxscience.guilds.config;

import net.minecraftforge.common.config.Config;
import parallaxscience.guilds.Guilds;

/**
 * Interface for the general section of the config file
 * @see Config
 * @author Tristan Jay
 */
@Config(modid = Guilds.MODID)
public class GeneralConfig
{
    /**
     * The amount of characters a guild or alliance can have in their name
     */
    @Config.Comment({
            "The amount of characters a guild or alliance can have in their name",
            "Default: 20"
    })
    @Config.Name("Maximum Name Length")
    public static int maxCharLength = 20;
}
