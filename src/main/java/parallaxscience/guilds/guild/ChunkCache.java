package parallaxscience.guilds.guild;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import parallaxscience.guilds.Guilds;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that is used to store and manage claimed chunk information
 * Holds the master list of claimed chunks
 * @author Tristan Jay
 */
public final class ChunkCache
{
    /**
     * Filepath to the ChunkCache save file location
     */
    private static final String fileName = "world/Guilds_ChunkCache.dat";

    /**
     * List of all of the claimed chunks and the owning guild
     * Implements a double-HashMap for fast indexing
     * @see HashMap
     */
    private static HashMap<Integer, HashMap<Integer, String>> chunkMap;

    /**
     * Initialize function for the class
     * Attempts to load the chunk data from file
     * If no chunk data is found, create a new HashMap
     */
    @SuppressWarnings("unchecked")
    public static void initialize()
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            chunkMap = (HashMap<Integer, HashMap<Integer,String>>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch(Exception e)
        {
            chunkMap = new HashMap<>();
        }
    }

    /**
     * Returns the name of the owner of a chunk
     * @param x X coordinate for the chunk
     * @param z Z coordinate for the chunk
     * @return String name of the owning guild
     */
    public static String getChunkOwner(int x, int z)
    {
        if(!chunkMap.containsKey(x)) return null;
        return chunkMap.get(x).get(z);
    }

    /**
     * Returns the name of the owner of a block
     * @param blockPos BlockPos of the block
     * @return String name of the owning guild
     */
    public static String getChunkOwner(BlockPos blockPos)
    {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        return getChunkOwner(chunkPos.x, chunkPos.z);
    }

    /**
     * Returns the name of the owner of a chunk
     * @param chunkPos ChunkPos of the chunk
     * @return String name of the owning guild
     */
    public static String getChunkOwner(ChunkPos chunkPos)
    {
        return getChunkOwner(chunkPos.x, chunkPos.z);
    }

    /**
     * Checks to see if the chunk is connected to the rest of the guild's territory
     * @param chunkPos ChunkPos of the chunk
     * @param guild Guild object reference
     * @return true if the chunk is connected to the rest of the guild territory
     */
    public static boolean isConnected(ChunkPos chunkPos, Guild guild)
    {
        if(guild.getTerritoryCount() == 0) return true;

        String guildName = guild.getGuildName();

        int x = chunkPos.x;
        int z = chunkPos.z;
        if(chunkMap.containsKey(x))
        {
            if(guildName.equals(chunkMap.get(x).get(z + 1))) return true;
            else if(guildName.equals(chunkMap.get(x).get(z - 1))) return true;
        }
        if(chunkMap.containsKey(x + 1))
        {
            if(guildName.equals(chunkMap.get(x + 1).get(z))) return true;
        }
        if(chunkMap.containsKey(x - 1))
        {
            return guildName.equals(chunkMap.get(x - 1).get(z));
        }
        return false;
    }

    /**
     * Sets the owner of a chunk
     * @param chunkPos ChunkPos of the chunk
     * @param guildName String name of the guild
     */
    public static void setChunkOwner(ChunkPos chunkPos, String guildName)
    {
        int x = chunkPos.x;
        int z = chunkPos.z;
        if(chunkMap.containsKey(x))
        {
            if(chunkMap.containsKey(z))
            {
                chunkMap.get(x).replace(z, guildName);
            }
            else
            {
                chunkMap.get(x).put(z, guildName);
            }
        }
        else
        {
            HashMap<Integer, String> temp = new HashMap<>();
            temp.put(z, guildName);
            chunkMap.put(x, temp);
        }
    }

    /**
     * Removes a chunk from a guild's territory
     * Removes the chunk from the chunk list
     * @param chunkPos ChunkPos of the chunk
     */
    public static void removeChunkOwner(ChunkPos chunkPos)
    {
        int x = chunkPos.x;
        int z = chunkPos.z;
        if(chunkMap.get(x).get(z) != null) chunkMap.get(x).remove(z);
    }

    /**
     * Remove all of a guild's claimed chunks
     * @param guildName String name of the guild
     */
    static void removeAllClaimed(String guildName)
    {
        for(Map.Entry<Integer, HashMap<Integer, String>> entry : chunkMap.entrySet())
        {
            for(Map.Entry<Integer, String> subEntry : entry.getValue().entrySet())
            {
                if(subEntry.getValue().equals(guildName)) chunkMap.get(entry.getKey()).remove(subEntry.getKey());
            }
        }
    }

    /**
     * Saves the guild data to file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save()
    {
        File file = new File(fileName);
        try
        {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(chunkMap);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {
            Guilds.logger.info("ERROR: IOException on chunk cache file save");
        }
    }
}
