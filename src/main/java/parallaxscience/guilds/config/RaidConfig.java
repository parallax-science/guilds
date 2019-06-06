package parallaxscience.guilds.config;

import net.minecraftforge.common.config.Config;
import parallaxscience.guilds.Guilds;

@Config(modid = Guilds.MODID, category = "raids")
public class RaidConfig {

    @Config.Comment({
            "How long the preparation phase lasts before the raid starts (in seconds)",
            "Default: 600"
    })
    @Config.Name("Preparation Phase Duration")
    public static int prepSeconds = 600;

    @Config.Comment({
            "The raid duration (in seconds)",
            "Once the time is up, the defenders will win the raid",
            "Default: 900"
    })
    @Config.Name("Raid Duration")
    public static int raidSeconds = 900;
}
