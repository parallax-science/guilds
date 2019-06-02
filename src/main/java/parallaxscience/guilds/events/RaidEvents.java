package parallaxscience.guilds.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;

import java.util.UUID;

public class RaidEvents {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDeath(LivingDeathEvent event)
    {
        EntityLivingBase entityLiving = event.getEntityLiving();
        if(entityLiving instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entityLiving;
            UUID playerID = player.getUniqueID();
            Guild guild = GuildCache.getPlayerGuild(playerID);
            if(guild != null)
            {
                Raid raid = RaidCache.getPlayerRaid(playerID);
                if(raid != null)
                {
                    if(raid.isActive())
                    {
                        raid.removePlayer(playerID);
                        player.connection.disconnect(new TextComponentString("You have been slain and are out of the fight!"));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer entityPlayer = event.player;
        if(entityPlayer instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
            Guild guild = GuildCache.getPlayerGuild(player.getUniqueID());
            if(guild != null)
            {
                Raid raid = RaidCache.getRaid(guild.getGuildName());
                if(raid != null)
                {
                    if(raid.isActive()) player.connection.disconnect(new TextComponentString("Your guild is currently being raided!"));
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        EntityPlayer entityPlayer = event.player;
        if(entityPlayer instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
            UUID playerID = player.getUniqueID();
            if(GuildCache.getPlayerGuild(playerID) != null)
            {
                Raid raid = RaidCache.getPlayerRaid(playerID);
                if(raid != null)
                {
                    raid.removePlayer(playerID);
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockDrop(BlockEvent.HarvestDropsEvent event)
    {
        if(event.getWorld().isRemote) return;

        BlockPos blockPos = event.getPos();
        String guildName = ChunkCache.getChunkOwner(blockPos);
        if(guildName != null)
        {
            Raid raid = RaidCache.getRaid(guildName);
            if(raid != null)
            {
                if(raid.isActive())
                {
                    event.getDrops().clear();
                    event.setDropChance(0.0f);
                }
            }
        }
    }
}
