package parallaxscience.guilds.config;

import net.minecraftforge.common.config.Config;
import parallaxscience.guilds.Guilds;

@Config(modid = Guilds.MODID)
public class GeneralConfig {

    @Config.Comment({
            "The amount of characters a guild or alliance can have in their name",
            "Default: 20"
    })
    @Config.Name("Maximum Name Length")
    public static int maxCharLength = 20;
}
