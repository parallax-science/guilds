package parallaxscience.guilds.raid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import parallaxscience.guilds.Guilds;
import parallaxscience.guilds.events.RaidEvents;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.utility.MessageUtility;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class that is used to store and manage raid information
 * Holds the master list of raids, as well as the chunk restore list
 * @author Tristan Jay
 */
public class RaidCache
{
    /**
     * Filepath to the RaidCache save file location
     */
    private final static String fileName = "world/Guilds_RaidsCache.dat";

    /**
     * HashMap of raids
     * Key is the name of the the defending guild
     * @see HashMap
     */
    private static HashMap<String, Raid> raids;

    /**
     * Minecraft world instance
     * Used for the chunk restore
     */
    private static World world;

    /**
     * List of blocks to be restored
     * Added whenever a block is changed during a raid
     * Removed whenever a raid is over
     * The first key is the name of the raid
     */
    private static HashMap<String, HashMap<BlockPos, IBlockState>> blockRestore;

    /**
     * RaidEvents instance
     * Used to register and deregister the raid events
     * @see RaidEvents
     */
    private static RaidEvents raidEvents;

    /**
     * Whether or not there is at least one raid active
     * Used to handle the raid event registration
     */
    private static boolean isActive;

    /**
     * Initialize function for the class
     * Instantiates and initializes variables in the cache
     */
    public static void initialize()
    {
        raids = new HashMap<>();
        raidEvents = new RaidEvents();
        blockRestore = new HashMap<>();
    }

    /**
     * Returns the player's raid
     * Returns null if the player is not currently part of a raid
     * @param player UUID of player
     * @return Raid that the player is a part of
     * @see Raid
     */
    public static Raid getPlayerRaid(UUID player)
    {
        for(Map.Entry<String, Raid> raidEntry : raids.entrySet())
        {
            Raid raid = raidEntry.getValue();
            if(raid.isRaider(player)) return raid;
        }
        return null;
    }

    /**
     * Returns the reference to the named raid
     * @param raidName String name of the raid, same as defending guild name
     * @return Raid reference
     * @see Raid
     */
    public static Raid getRaid(String raidName)
    {
        return raids.get(raidName);
    }

    /**
     * Creates a new raid and adds it to the raid list
     * @param raidName String name of the raid
     * @param primaryAttacker UUID of primary attacker
     */
    public static void createRaid(String raidName, UUID primaryAttacker)
    {
        raids.put(raidName, new Raid(raidName, primaryAttacker));
        blockRestore.put(raidName, new HashMap<>());
        if(!isActive) MinecraftForge.EVENT_BUS.register(raidEvents);
    }

    /**
     * Stops a raid, removes it from the raid list, and calls the block restore
     * Called whenever either the timer runs out, or one side kills the other
     * @param raidName String name of raid
     * @param defenseWon true if defense has won
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void stopRaid(String raidName, boolean defenseWon)
    {
        Raid raid = getRaid(raidName);
        raid.stopTimer();
        GuildCache.getGuild(raidName).resetRaidInterval();
        MessageUtility.raidMessageAll("The raid on " + raidName + " is now over!");
        if(defenseWon) MessageUtility.raidMessageAll(raidName + " has successfully held off the attackers!");
        else MessageUtility.raidMessageAll("The attackers have successfully raided " + raidName + "!");

        for(Map.Entry<BlockPos, IBlockState> blocks : blockRestore.get(raidName).entrySet())
        {
            restoreBlock(blocks.getKey(), blocks.getValue());
        }
        blockRestore.remove(raidName);

        if(blockRestore.isEmpty())
        {
            File file = new File(fileName);
            file.delete();
        }
        else saveRaid();

        raids.remove(raidName);
        if(raids.isEmpty())
        {
            MinecraftForge.EVENT_BUS.unregister(raidEvents);
            isActive = false;
        }
    }

    /**
     * Removes a raid from the raid list
     * Called whenever all of one side has left the raid before the active phase has started
     * @param raid String name of the raid
     */
    static void cancelRaid(String raid)
    {
        raids.remove(raid);
    }

    /**
     * Adds a block to the raid restore list
     * @param raid String name of the raid
     * @param blockPos BlockPos of the block
     * @param blockState IBlockState of the block
     */
    public static void addRestoreBlock(String raid, BlockPos blockPos, IBlockState blockState)
    {
        HashMap<BlockPos, IBlockState> guildBlockRestore = blockRestore.get(raid);
        if(!guildBlockRestore.containsKey(blockPos)) guildBlockRestore.put(blockPos, blockState);
        saveRaid();
    }

    /**
     * Saves the raid data to file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveRaid()
    {
        File file = new File(fileName);
        try
        {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(blockRestore);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {
            Guilds.logger.info("ERROR: IOException on raid cache file save");
        }
    }

    /**
     * Used to restore blocks that had not been restored before the server crashed
     * Called whenever the server starts up
     * @param event FMLServerStartingEvent
     * @see FMLServerStartingEvent
     */
    @SuppressWarnings("unchecked")
    public static void massRestore(FMLServerStartingEvent event)
    {
        world = event.getServer().getWorld(0);
        try
        {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            HashMap<String, HashMap<BlockPos, IBlockState>> oldRestore = (HashMap<String, HashMap<BlockPos, IBlockState>>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            for(Map.Entry<String, HashMap<BlockPos, IBlockState>> raids : oldRestore.entrySet())
            {
                for(Map.Entry<BlockPos, IBlockState> blocks : raids.getValue().entrySet())
                {
                    restoreBlock(blocks.getKey(), blocks.getValue());
                }
            }
        }
        catch(Exception e)
        {
            Guilds.logger.info("No chunks to restore!");
        }
    }

    /**
     * Sets a block in a certain position
     * Used by the chunk restore to restore a block to its original state
     * @param blockPos BlockPos of block to be restored
     * @param iBlockState IBlockState of block to be restored
     * @see BlockPos
     * @see IBlockState
     */
    private static void restoreBlock(BlockPos blockPos, IBlockState iBlockState)
    {
        if(!world.isRemote)
        {
            world.setBlockState(blockPos, iBlockState);
        }
    }
}
