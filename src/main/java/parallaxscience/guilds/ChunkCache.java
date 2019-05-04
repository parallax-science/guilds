package parallaxscience.guilds;


import parallaxscience.guilds.guild.Guild;

import java.util.HashMap;

public class ChunkCache {

    private static HashMap<Integer, HashMap<Integer, Guild>> chunkMap;

    public static void initialize(int z)
    {
        chunkMap = new HashMap<>();
    }

    public static Guild getChunkOwner(int x, int z)
    {
        //Chunk is not claimed?
        return chunkMap.get(x).get(z);
    }

    public static void setChunkOwner(int x, int z, Guild guild)
    {
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
}
