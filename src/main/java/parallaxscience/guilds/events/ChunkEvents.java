package parallaxscience.guilds.events;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSpade;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;
import java.util.ArrayList;
import java.util.List;

public class ChunkEvents {

    @SubscribeEvent
    @SuppressWarnings("unused")
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

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if(event.getWorld().isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
        if(owner == null) return;

        Raid raid = RaidCache.getRaid(owner.getGuildName());
        if(raid != null)
        {
            if(raid.isActive())
            {
                BlockPos blockPos = event.getPos();
                IBlockState iBlockState = event.getWorld().getBlockState(blockPos);
                if(iBlockState.getBlock().hasTileEntity(iBlockState))
                {
                    event.setCanceled(true);
                    player.sendMessage(new TextComponentString("You cannot break this block during a raid!"));
                }
                else
                {
                    RaidCache.addRestoreBlock(raid.getDefendingGuild(), blockPos, iBlockState);
                }
            }
            else if(!owner.isMember(player.getUniqueID()))
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot break blocks in another guilds territory!"));
            }
        }
        else if(!owner.isMember(player.getUniqueID()))
        {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString("You cannot break blocks in another guilds territory!"));
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onCropTrample(BlockEvent.FarmlandTrampleEvent event)
    {
        if(event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
            if(owner == null) return;

            if(!owner.isMember((player.getUniqueID())))
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot trample crops in another guilds territory!"));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBucketUse(FillBucketEvent event)
    {
        if(event.getWorld().isRemote) return;

        RayTraceResult result = event.getTarget();
        if(result == null) return;

        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP)
        {
            Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(result.getBlockPos().offset(result.sideHit)));
            if(owner == null) return;

            EntityPlayerMP player = (EntityPlayerMP) entity;
            Raid raid = RaidCache.getRaid(owner.getGuildName());
            if(owner.isMember((player.getUniqueID())))
            {
                if(raid != null)
                {
                    if(raid.isActive())
                    {
                        event.setCanceled(true);
                        player.sendMessage(new TextComponentString("You cannot use fluids during a raid!"));
                    }
                }
            }
            else
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot place fluids in another clans territory!"));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event)
    {
        if(event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
            if(owner == null) return;

            Raid raid = RaidCache.getRaid(owner.getGuildName());
            if(raid != null)
            {
                if(raid.isActive())
                {
                    BlockPos blockPos = event.getPos();
                    RaidCache.addRestoreBlock(raid.getDefendingGuild(), blockPos, Blocks.AIR.getDefaultState());
                }
                else
                {
                    event.setCanceled(true);
                    player.sendMessage(new TextComponentString("You cannot place blocks in another guilds territory!"));
                }
            }
            else if(!owner.isMember((player.getUniqueID())))
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot place blocks in another guilds territory!"));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onMultiBlockPlaced(BlockEvent.EntityMultiPlaceEvent event)
    {
        if(event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
            if(owner == null) return;

            Raid raid = RaidCache.getRaid(owner.getGuildName());
            if(raid != null)
            {
                if(raid.isActive())
                {
                    BlockPos blockPos = event.getPos();
                    RaidCache.addRestoreBlock(raid.getDefendingGuild(), blockPos, Blocks.AIR.getDefaultState());
                }
                else if(!owner.isMember((player.getUniqueID())))
                {
                    event.setCanceled(true);
                    player.sendMessage(new TextComponentString("You cannot place blocks in another guilds territory!"));
                }
            }
            if(!owner.isMember((player.getUniqueID())))
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot place blocks in another guilds territory!"));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPortalSpawn(BlockEvent.PortalSpawnEvent event)
    {
        if(event.getWorld().isRemote) return;
        if(ChunkCache.getChunkOwner(event.getPos()) != null) event.setCanceled(true);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getWorld().isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
        if(owner == null) return;

        Raid raid = RaidCache.getRaid(owner.getGuildName());
        if(raid != null)
        {
            if(raid.isActive())
            {
                IBlockState iBlockState = event.getWorld().getBlockState(event.getPos());
                Block block = iBlockState.getBlock();
                if(block.hasTileEntity(iBlockState) || (block == Blocks.GRASS && event.getItemStack().getItem() instanceof ItemSpade))
                {
                    event.setCanceled(true);
                    player.sendMessage(new TextComponentString("You cannot use this during a raid!"));
                }
            }
            else if(!owner.isMember((player.getUniqueID())))
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot interact with blocks in another guilds territory!"));
            }
        }
        else if(!owner.isMember((player.getUniqueID())))
        {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString("You cannot interact with blocks in another guilds territory!"));
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onUseJessica(UseHoeEvent event)
    {
        if(event.getWorld().isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(event.getPos()));
        if(owner == null) return;

        Raid raid = RaidCache.getRaid(owner.getGuildName());
        if(raid != null)
        {
            if(raid.isActive())
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot use a hoe during a raid!"));
            }
            else if(!owner.isMember((player.getUniqueID())))
            {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot use a hoe in another guilds territory!"));
            }
        }
        else if(!owner.isMember((player.getUniqueID())))
        {
            event.setCanceled(true);
            player.sendMessage(new TextComponentString("You cannot use a hoe in another guilds territory!"));
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onDetonate(ExplosionEvent.Detonate event)
    {
        if(event.getWorld().isRemote) return;

        List<BlockPos> blocks = event.getAffectedBlocks();
        ArrayList<BlockPos> removeBlocks = new ArrayList<>();
        for(BlockPos blockPos : blocks)
        {
            String ownerName = ChunkCache.getChunkOwner(blockPos);
            if(ownerName != null)
            {
                Raid raid = RaidCache.getRaid(ownerName);
                if(raid == null) removeBlocks.add(blockPos);
                else
                {
                    if(raid.isActive())
                    {
                        IBlockState iBlockState = event.getWorld().getBlockState(blockPos);
                        if(iBlockState.getBlock().hasTileEntity(iBlockState)) removeBlocks.add(blockPos);
                        else
                        {
                            RaidCache.addRestoreBlock(raid.getDefendingGuild(), blockPos, iBlockState);
                        }
                    }
                    else removeBlocks.add(blockPos);
                }
            }
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
                if(ownerName != null)
                {
                    Guild guild = GuildCache.getGuild(ownerName);
                    if(guild.isMember(entity.getUniqueID()))
                    {
                        Raid raid = RaidCache.getRaid(guild.getGuildName());
                        if(raid != null)
                        {
                            if(!raid.isActive()) removeEntities.add(entity);
                        }
                        else removeEntities.add(entity);
                    }
                }

            }
        }
        for(Entity entity : removeEntities)
        {
            entities.remove(entity);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLivingDamage(LivingDamageEvent event)
    {
        Entity entity = event.getEntityLiving();
        if(entity.getEntityWorld().isRemote) return;

        Guild owner = GuildCache.getGuild(ChunkCache.getChunkOwner(entity.getPosition()));
        if(owner == null) return;

        if(entity instanceof EntityPlayerMP)
        {
            if(owner.isMember(entity.getUniqueID()))
            {
                Raid raid = RaidCache.getRaid(owner.getGuildName());
                if(raid != null)
                {
                    if(!raid.isActive()) event.setCanceled(true);
                }
                else event.setCanceled(true);
            }
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
