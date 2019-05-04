package parallaxscience.guilds.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import parallaxscience.guilds.ChunkCache;
import parallaxscience.guilds.guild.Guild;

public class ClanEvents {

    @SubscribeEvent
    public void onEnterChunk(EntityEvent.EnteringChunk event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP)
        {
            Guild oldOwner = ChunkCache.getChunkOwner(event.getOldChunkX(), event.getOldChunkZ());
            Guild newOwner = ChunkCache.getChunkOwner(event.getNewChunkX(), event.getNewChunkZ());

            if(oldOwner != newOwner)
            {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                if(newOwner != null) player.sendMessage(new TextComponentString("Entering the Territory of " + newOwner));
                else player.sendMessage(new TextComponentString("Entering Wilderness."));
            }
        }
    }
}
