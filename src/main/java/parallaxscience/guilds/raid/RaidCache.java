package parallaxscience.guilds.raid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import parallaxscience.guilds.events.RaidEvents;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidCache {

    private final static String fileName = "world/Guilds_RaidsCache.dat";

    private static HashMap<String, Raid> raids;

    private static World world;
    private static HashMap<String, HashMap<BlockPos, IBlockState>> blockRestore;

    private static RaidEvents raidEvents;
    private static boolean isActive;

    public static void initialize()
    {
        raids = new HashMap<>();
        raidEvents = new RaidEvents();
        blockRestore = new HashMap<>();
    }

    public static Raid getPlayerRaid(UUID player)
    {
        for(Map.Entry<String, Raid> raidEntry : raids.entrySet())
        {
            Raid raid = raidEntry.getValue();
            if(raid.isRaider(player)) return raid;
        }
        return null;
    }

    public static Raid getRaid(String raidName)
    {
        return raids.get(raidName);
    }

    public static void createRaid(String raidName, UUID primaryAttacker)
    {
        raids.put(raidName, new Raid(raidName, primaryAttacker));
        blockRestore.put(raidName, new HashMap<>());
        if(!isActive) MinecraftForge.EVENT_BUS.register(raidEvents);
    }

    static void stopRaid(String raid, boolean defenseWon)
    {
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        players.sendMessage(new TextComponentString("The raid on " + raid + " is now over!"));
        if(defenseWon) players.sendMessage(new TextComponentString(raid + " has successfully held off the attackers!"));
        else players.sendMessage(new TextComponentString("The attackers have successfully raided " + raid + "!"));

        for(Map.Entry<BlockPos, IBlockState> blocks : blockRestore.get(raid).entrySet())
        {
            if(!world.isRemote) world.setBlockState(blocks.getKey(), blocks.getValue());
        }
        blockRestore.remove(raid);

        if(blockRestore.isEmpty())
        {
            File file = new File(fileName);
            file.delete();
        }
        else saveRaid();

        raids.remove(raid);
        if(raids.isEmpty())
        {
            MinecraftForge.EVENT_BUS.unregister(raidEvents);
            isActive = false;
        }
    }

    static void cancelRaid(String raid)
    {
        raids.remove(raid);
    }

    public static void addRestoreBlock(String raid, BlockPos blockPos, IBlockState blockState)
    {
        blockRestore.get(raid).put(blockPos, blockState);
        saveRaid();
    }

    private static void saveRaid()
    {
        File file = new File(fileName);
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch(Exception e)
            {

            }
        }

        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(blockRestore);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {

        }
    }

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
                    if(!world.isRemote) world.setBlockState(blocks.getKey(), blocks.getValue());
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("No chunks to restore!");
        }
    }
}
