package parallaxscience.guilds.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.guild.Guild;

import java.util.ArrayList;
import java.util.List;

public class ChunkEvents {

    @SubscribeEvent
    public void onEnterChunk(EntityEvent.EnteringChunk event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP)
        {
            String oldOwner = ChunkCache.getChunkOwner(event.getOldChunkX(), event.getOldChunkZ());
            String newOwner = ChunkCache.getChunkOwner(event.getNewChunkX(), event.getNewChunkZ());
            if(newOwner == null)
            {
                if(oldOwner != null) entity.sendMessage(new TextComponentString("Entering Wilderness."));
            }
            else if(!newOwner.equals(oldOwner)) entity.sendMessage(new TextComponentString("Entering the Territory of " + newOwner));
        }
    }

    private void chunkProtection(BlockEvent event, EntityPlayerMP player, String cancelMessage)
    {
        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
        if(owner == null) return;

        if(!owner.isMember((player.getUniqueID())))
        {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString(cancelMessage));
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if(event.getWorld().isRemote) return;
        chunkProtection(event, (EntityPlayerMP) event.getPlayer(), "You cannot break blocks in another guilds territory!");
    }

    @SubscribeEvent
    public void onCropTrample(BlockEvent.FarmlandTrampleEvent event)
    {
        if(event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP) chunkProtection(event, (EntityPlayerMP) entity, "You cannot trample crops in another guilds territory!");
    }

    @SubscribeEvent
    public void onFluidBlockPlaced(BlockEvent.FluidPlaceBlockEvent event)
    {
        if(event.getWorld().isRemote) return;
        String owner = ChunkCache.getChunkOwner(event.getPos());
        if(owner != null)
        {
            String sourceOwner = ChunkCache.getChunkOwner(event.getLiquidPos());
            if(sourceOwner != null) if(!owner.equals(sourceOwner)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event)
    {
        if(event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP) chunkProtection(event, (EntityPlayerMP) entity, "You cannot place blocks in another guilds territory!");
    }

    @SubscribeEvent
    public void onMultiBlockPlaced(BlockEvent.EntityMultiPlaceEvent event)
    {
        if(event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP) chunkProtection(event, (EntityPlayerMP) entity, "You cannot place blocks in another guilds territory!");
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getWorld().isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
        if(owner == null) return;

        if(!owner.isMember((player.getUniqueID())))
        {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString("You cannot interact with blocks in another guilds territory!"));
        }
    }

    @SubscribeEvent
    public void onDetonate(ExplosionEvent.Detonate event)
    {
        if(event.getWorld().isRemote) return;

        List<BlockPos> blocks = event.getAffectedBlocks();
        ArrayList<BlockPos> removeBlocks = new ArrayList<>();
        for(BlockPos blockPos : blocks)
        {
            if(ChunkCache.getChunkOwner(blockPos) != null) removeBlocks.add(blockPos);
        }
        for(BlockPos blockPos : removeBlocks)
        {
            blocks.remove(blockPos);
        }

        List<Entity> entities = event.getAffectedEntities();
        ArrayList<Entity> removeEntities = new ArrayList<>();
        for(Entity entity : entities)
        {
            if(entity instanceof EntityPlayerMP)
            {
                BlockPos entityPos = entity.getPosition();
                String ownerName = ChunkCache.getChunkOwner(entityPos);
                if(ownerName != null) if(GuildCache.getGuild(ownerName).isMember(entity.getUniqueID()))
                   removeEntities.add(entity);
            }
        }
        for(Entity entity : removeEntities)
        {
            entities.remove(entity);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event)
    {
        Entity entity = event.getEntityLiving();
        if(entity.getEntityWorld().isRemote) return;

        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(entity.getPosition()));
        if(owner == null) return;

        if(entity instanceof EntityPlayerMP)
        {
            if(owner.isMember(entity.getUniqueID())) event.setCanceled(true);
        }
        else
        {
            Entity damageSource = event.getSource().getTrueSource();
            if(damageSource == null) event.setCanceled(true);
            else if(entity instanceof EntityTameable)
            {
                EntityTameable pet = (EntityTameable) entity;
                if(pet.isTamed())
                {
                    if(owner.isMember(pet.getOwnerId()) && !owner.isMember(damageSource.getUniqueID())) event.setCanceled(true);
                }
            }
            else if(entity instanceof EntityAnimal)
            {
                if(!owner.isMember(event.getSource().getTrueSource().getUniqueID())) event.setCanceled(true);
            }
        }
    }
}
