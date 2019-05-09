package parallaxscience.guilds;

import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import parallaxscience.guilds.commands.CommandGuild;

@Mod(modid = Guilds.MODID, name = Guilds.NAME, version = Guilds.VERSION, acceptedMinecraftVersions = Guilds.MC_VERSION)
public class Guilds
{
    public static final String MODID = "guilds";
    public static final String NAME = "The Guilds Mod";
    public static final String VERSION = "ALPHA 1.0";
    public static final String MC_VERSION = "[1.12.2]";

    public static final Logger LOGGER = LogManager.getLogger(Guilds.MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info(Guilds.NAME + "says hi!");
        GuildCache.initialize();
        ChunkCache.initialize();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandGuild());
    }
}