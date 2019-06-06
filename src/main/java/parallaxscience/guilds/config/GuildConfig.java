package parallaxscience.guilds.config;

import net.minecraftforge.common.config.Config;
import parallaxscience.guilds.Guilds;

@Config(modid = Guilds.MODID, category = "guilds")
public class GuildConfig {

    @Config.Comment({
            "The amount of land a guild can claim per member",
            "Default: 50"
    })
    @Config.Name("Number of Claims Per Member")
    public static int claimPerPlayer = 50;
}
