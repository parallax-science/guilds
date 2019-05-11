package parallaxscience.guilds.guild;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class ChunkCache
{
    private static final String fileName = "world/Guilds_ChunkCache.dat";

    private static HashMap<Integer, HashMap<Integer, String>> chunkMap;

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

    public static String getChunkOwner(int x, int z)
    {
        if(!chunkMap.containsKey(x)) return null;
        return chunkMap.get(x).get(z);
    }

    public static String getChunkOwner(BlockPos blockPos)
    {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        return getChunkOwner(chunkPos.x, chunkPos.z);
    }

    public static String getChunkOwner(ChunkPos chunkPos)
    {
        return getChunkOwner(chunkPos.x, chunkPos.z);
    }

    public static boolean isConnected(ChunkPos chunkPos, Guild guild)
    {
        if(guild.getTerritoryCount() == 0) return true;

        int x = chunkPos.x;
        int z = chunkPos.z;
        if(chunkMap.containsKey(x))
        {
            if(guild.equals(chunkMap.get(x).get(z + 1))) return true;
            else if(guild.equals(chunkMap.get(x).get(z - 1))) return true;
        }
        if(chunkMap.containsKey(x + 1))
        {
            if(guild.equals(chunkMap.get(x + 1).get(z))) return true;
        }
        if(chunkMap.containsKey(x - 1))
        {
            if(guild.equals(chunkMap.get(x - 1).get(z))) return true;
        }
        return false;
    }

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

    public static void removeChunkOwner(ChunkPos chunkPos)
    {
        int x = chunkPos.x;
        int z = chunkPos.z;
        if(chunkMap.get(x).get(z) != null) chunkMap.get(x).remove(z);
    }

    public static void removeAllClaimed(String guildName)
    {
        for(Map.Entry<Integer, HashMap<Integer, String>> entry: chunkMap.entrySet())
        {
            int x = entry.getKey();
            for(Map.Entry<Integer, String> subEntry : entry.getValue().entrySet())
            {
                int z = entry.getKey();
                if(subEntry.getValue().equals(guildName)) chunkMap.get(x).remove(z);
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
