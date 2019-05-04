package parallaxscience.guilds;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}