package parallaxscience.guilds;

import net.minecraft.util.math.BlockPos;
import parallaxscience.guilds.guild.Guild;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class ChunkCache
{
    private static final String fileName = "world/Guilds_ChunkCache.dat";

    private static HashMap<Integer, HashMap<Integer, Guild>> chunkMap;

    public static void initialize()
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            chunkMap = (HashMap<Integer, HashMap<Integer,Guild>>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch(Exception e)
        {
            chunkMap = new HashMap<>();
        }
    }

    public static Guild getChunkOwner(int x, int z)
    {
        if(!chunkMap.containsKey(x)) return null;
        return chunkMap.get(x).get(z);
    }

    public static Guild getBlockOwner(BlockPos blockPos)
    {
        return getChunkOwner(blockPos.getX()/16, blockPos.getZ()/16);
    }

    public static void setChunkOwner(BlockPos blockPos, Guild guild)
    {
        int x = blockPos.getX()/16;
        int z = blockPos.getZ()/16;
        if(chunkMap.containsKey(x))
        {
            if(chunkMap.containsKey(z))
            {
                chunkMap.get(x).replace(z, guild);
            }
            else
            {
                chunkMap.get(x).put(z, guild);
            }
        }
        else
        {
            HashMap<Integer, Guild> temp= new HashMap<>();
            temp.put(z, guild);
            chunkMap.put(x, temp);
        }
    }

    public static void removeChunkOwner(BlockPos blockPos)
    {
        int x = blockPos.getX()/16;
        int z = blockPos.getZ()/16;
        if(chunkMap.get(x).get(z) != null) chunkMap.get(x).remove(z);
    }

    public static void removeAllClaimed(Guild guild)
    {
        for(Map.Entry<Integer, HashMap<Integer, Guild>> entry: chunkMap.entrySet())
        {
            int x = entry.getKey();
            for(Map.Entry<Integer, Guild> subEntry : entry.getValue().entrySet())
            {
                int z = entry.getKey();
                if(subEntry.getValue().equals(guild)) chunkMap.get(x).remove(z);
            }
        }
    }

    public static void save()
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
            objectOutputStream.writeObject(chunkMap);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e)
        {

        }
    }
}
