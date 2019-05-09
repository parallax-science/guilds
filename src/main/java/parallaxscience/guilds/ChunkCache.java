package parallaxscience.guilds;

import net.minecraft.util.math.BlockPos;
import parallaxscience.guilds.guild.Guild;
import java.util.HashMap;
import java.util.Map;

public class ChunkCache
{
    private static HashMap<Integer, HashMap<Integer, Guild>> chunkMap;

    public static void initialize()
    {
        chunkMap = new HashMap<>();
    }

    public static Guild getChunkOwner(int x, int z)
    {
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
}
