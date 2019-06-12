package parallaxscience.guilds;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import parallaxscience.guilds.alliance.AllianceCache;
import parallaxscience.guilds.commands.CommandAlliance;
import parallaxscience.guilds.commands.CommandGuild;
import parallaxscience.guilds.commands.CommandRaid;
import parallaxscience.guilds.events.ChunkEvents;
import parallaxscience.guilds.events.GuildEvents;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.raid.RaidCache;
import parallaxscience.guilds.utility.MessageUtility;

/**
 * The main class for the Guilds Mod
 * This is where the name and id of the mod are declared, as well as where everything is initialized
 * @author Tristan Jay
 */
@Mod(modid = Guilds.MODID, name = Guilds.NAME, version = Guilds.VERSION, acceptedMinecraftVersions = Guilds.MC_VERSION, acceptableRemoteVersions = "*")
public class Guilds
{
    /**
     * The MODID is the unique id for the mod
     */
    public static final String MODID = "guilds";

    /**
     * The user friendly name of the mod
     */
    static final String NAME = "The Guilds Mod";

    /**
     * The mod version
     */
    static final String VERSION = "ALPHA 1.0";

    /**
     * The version of Minecraft that this mod is compatible with
     */
    static final String MC_VERSION = "[1.12.2]";

    /**
     * Called on Forge startup
     * Used primarily for event registration and config management
     * @param event FMLPreInitializationEvent
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new GuildEvents());
        MinecraftForge.EVENT_BUS.register(new ChunkEvents());
    }

    /**
     * Called after the preInit phase
     * The main function for mod code
     * This is where all blocks, items, etc are registered
     * @param event FMLInitializationEvent
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {
        GuildCache.initialize();
        AllianceCache.initialize();
        ChunkCache.initialize();
        RaidCache.initialize();
        MessageUtility.initialize();
    }

    /**
     * Called after the init phase
     * This is primarily used for mod compatibility
     * @param event FMLPostInitializationEvent
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {
    }

    /**
     * Called whenever the server starts
     * All commands are registered here
     * @param event FMLServerStartingEvent
     */
    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandGuild());
        event.registerServerCommand(new CommandAlliance());
        event.registerServerCommand(new CommandRaid());

        RaidCache.massRestore(event);
    }
}